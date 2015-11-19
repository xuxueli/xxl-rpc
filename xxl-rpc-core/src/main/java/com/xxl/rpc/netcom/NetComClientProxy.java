package com.xxl.rpc.netcom;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import com.xxl.rpc.netcom.NetComServerFactory.NetComTypeEnum;
import com.xxl.rpc.netcom.common.codec.RpcCallbackFuture;
import com.xxl.rpc.netcom.common.codec.RpcRequest;
import com.xxl.rpc.netcom.common.codec.RpcResponse;
import com.xxl.rpc.netcom.mina.client.MinaClientPool;
import com.xxl.rpc.netcom.mina.client.MinaClientPoolProxy;
import com.xxl.rpc.netcom.netty.client.NettyClientPool;
import com.xxl.rpc.netcom.netty.client.NettyClientPoolProxy;
import com.xxl.rpc.serialize.Serializer;
import com.xxl.rpc.util.HttpClientUtil;

/**
 * rpc proxy
 * @author xuxueli 2015-10-29 20:18:32
 */
public class NetComClientProxy implements FactoryBean<Object> {
	private static final Logger logger = LoggerFactory.getLogger(NetComClientProxy.class);	 
	// [tips01: save 30ms/100invoke. why why why??? with this logger, it can save lots of time.]
	
	// origin prop
	private String netcom_type = NetComTypeEnum.NETTY.name();
	private String serverAddress;
	private String serialize = Serializer.SerializeType.HESSIAN.name();
	private Class<?> iface;
	private boolean zookeeper_switch;
	private long timeoutMillis = 5000;
	
	// second prop	[tips02 : save 145ms/100invoke. Caused by hash method in HashMap.get invoked in every invoke ]
	private NetComTypeEnum netcomType;
	private Serializer serializer;
	
	public NetComClientProxy() {
		netcomType = NetComTypeEnum.getInstance(netcom_type);
		serializer = Serializer.getInstance(serialize);
	}
	public NetComClientProxy(String netcom_type, String serverAddress, String serialize, Class<?> iface, boolean zookeeper_switch, long timeoutMillis) {
		this.netcom_type = netcom_type;
		this.serverAddress = serverAddress;
		this.iface = iface;
		this.serialize = serialize;
		this.zookeeper_switch = zookeeper_switch;
		this.timeoutMillis = timeoutMillis;
		
		netcomType = NetComTypeEnum.getInstance(netcom_type);
		serializer = Serializer.getInstance(serialize);
	}
	
	public String getNetcom_type() {
		return netcom_type;
	}
	public void setNetcom_type(String netcom_type) {
		this.netcom_type = netcom_type;
	}
	public String getServerAddress() {
		return serverAddress;
	}
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	public Class<?> getIface() {
		return iface;
	}
	public void setIface(Class<?> iface) {
		this.iface = iface;
	}
	public String getSerialize() {
		return serialize;
	}
	public void setSerialize(String serialize) {
		this.serialize = serialize;
	}
	public boolean isZookeeper_switch() {
		return zookeeper_switch;
	}
	public void setZookeeper_switch(boolean zookeeper_switch) {
		this.zookeeper_switch = zookeeper_switch;
	}
	public long getTimeoutMillis() {
		return timeoutMillis;
	}
	public void setTimeoutMillis(long timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}

	@Override
	public Object getObject() throws Exception {
		return Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), new Class[] { iface },
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						// response
						RpcResponse response = null;
						
						// request
						RpcRequest request = new RpcRequest();
	                    request.setRequestId(UUID.randomUUID().toString());
	                    request.setCreateMillisTime(System.currentTimeMillis());
	                    request.setClassName(method.getDeclaringClass().getName());
	                    request.setMethodName(method.getName());
	                    request.setParameterTypes(method.getParameterTypes());
	                    request.setParameters(args);
	                    
	                    if (netcomType == NetComTypeEnum.MINA) {
	                    	// client pool
	                    	GenericObjectPool<MinaClientPoolProxy> clientPool = MinaClientPool.getPool(zookeeper_switch, serverAddress, request.getClassName(), serializer);
	                    	// client proxt
	                    	MinaClientPoolProxy clientPoolProxy = null;
							try {
								// future init
								RpcCallbackFuture future = new RpcCallbackFuture(request);
								RpcCallbackFuture.futurePool.put(request.getRequestId(), future);
								
								// rpc invoke
								clientPoolProxy = clientPool.borrowObject();
								clientPoolProxy.send(request);
								
								// future get
								response = future.get(timeoutMillis);
							} catch (Exception e) {
								logger.error("", e);
								throw e;
							} finally{
								RpcCallbackFuture.futurePool.remove(request.getRequestId());
								clientPool.returnObject(clientPoolProxy);
							}
							
						} else if (netcomType == NetComTypeEnum.JETTY) {
							byte[] requestBytes = serializer.serialize(request);
							byte[] responseBytes = HttpClientUtil.postRequest("http://127.0.0.1:9999/", requestBytes);
							response = (RpcResponse) serializer.deserialize(responseBytes, RpcResponse.class);
							
						} else {
							// client pool	[tips03 : may save 35ms/100invoke if move it to constructor, but it is necessary. cause by ConcurrentHashMap.get]
		                    GenericObjectPool<NettyClientPoolProxy> clientPool = NettyClientPool.getPool(zookeeper_switch, serverAddress, request.getClassName(), serializer);
		                    
		                    // client proxt
		                    NettyClientPoolProxy clientPoolProxy = null;
							try {
								// future init	[tips04 : may save 20ms/100invoke if remove and wait for channel instead, but it is necessary. cause by ConcurrentHashMap.get]
								RpcCallbackFuture future = new RpcCallbackFuture(request);
								RpcCallbackFuture.futurePool.put(request.getRequestId(), future);
								
								// rpc invoke
								clientPoolProxy = clientPool.borrowObject();
								clientPoolProxy.send(request);
								
								// future get
								response = future.get(timeoutMillis);
							} catch (Exception e) {
								logger.error("", e);
								throw e;
							} finally{
								RpcCallbackFuture.futurePool.remove(request.getRequestId());
								clientPool.returnObject(clientPoolProxy);
							}
						}
	                    
	                    // valid response
						if (response == null) {
							throw new Exception(">>>>>>>>>>> xxl-rpc netty response not found.");
						}
	                    if (response.isError()) {
	                        throw response.getError();
	                    } else {
	                        return response.getResult();
	                    }
					}
				});
	}
	@Override
	public Class<?> getObjectType() {
		return iface;
	}
	@Override
	public boolean isSingleton() {
		return false;
	}
	
}
