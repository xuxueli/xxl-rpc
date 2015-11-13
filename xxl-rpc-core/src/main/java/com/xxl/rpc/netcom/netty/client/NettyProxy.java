package com.xxl.rpc.netcom.netty.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import com.xxl.rpc.netcom.netty.codec.NettyRequest;
import com.xxl.rpc.netcom.netty.codec.NettyResponse;
import com.xxl.rpc.serialize.Serializer;

/**
 * rpc proxy
 * @author xuxueli 2015-10-29 20:18:32
 */
public class NettyProxy implements FactoryBean<Object> {
	private static final Logger logger = LoggerFactory.getLogger(NettyProxy.class);	 
	// [tips01: save 30ms/100invoke. why why why??? with this logger, it can save lots of time.]
	
	// origin prop
	private Class<?> iface;
	private String serverAddress;
	private String serialize;
	private boolean zookeeper_switch;
	private long timeoutMillis = 5000;
	
	// second prop	[tips02 : save 145ms/100invoke. Caused by hash method in HashMap.get invoked in every invoke ]
	private Serializer serializer;	
	
	public NettyProxy() {
		serializer = Serializer.getInstance(serialize);
	}
	public NettyProxy(String serverAddress, Class<?> iface, String serialize, boolean zookeeper_switch, long timeoutMillis) {
		this.serverAddress = serverAddress;
		this.iface = iface;
		this.serialize = serialize;
		this.zookeeper_switch = zookeeper_switch;
		this.timeoutMillis = timeoutMillis;
		
		serializer = Serializer.getInstance(serialize);
	}
	
	public Class<?> getIface() {
		return iface;
	}
	public void setIface(Class<?> iface) {
		this.iface = iface;
	}
	public String getServerAddress() {
		return serverAddress;
	}
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
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
						NettyResponse response = null;
						
						// request
						NettyRequest request = new NettyRequest();
	                    request.setRequestId(UUID.randomUUID().toString());
	                    request.setCreateMillisTime(System.currentTimeMillis());
	                    request.setClassName(method.getDeclaringClass().getName());
	                    request.setMethodName(method.getName());
	                    request.setParameterTypes(method.getParameterTypes());
	                    request.setParameters(args);
	                    
	                    // client pool	[tips03 : may save 35ms/100invoke if move it to constructor, but it is necessary. cause by ConcurrentHashMap.get]
	                    GenericObjectPool<NettyClientPoolProxy> clientPool = NettyClientPool.getPool(zookeeper_switch, serverAddress, request.getClassName(), serializer);
	                    
	                    // client proxt
	                    NettyClientPoolProxy clientPoolProxy = null;
						try {
							// future init	[tips04 : may save 20ms/100invoke if remove and wait for channel instead, but it is necessary. cause by ConcurrentHashMap.get]
							NettyClientCallbackFuture future = new NettyClientCallbackFuture(request);
							NettyClientCallbackFuture.futurePool.put(request.getRequestId(), future);
							
							// rpc invoke
							clientPoolProxy = clientPool.borrowObject();
							clientPoolProxy.send(request);
							
							// future get
							response = future.get(timeoutMillis);
						} catch (Exception e) {
							logger.error("", e);
							throw e;
						} finally{
							NettyClientCallbackFuture.futurePool.remove(request.getRequestId());
							clientPool.returnObject(clientPoolProxy);
						}
	                    
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
