package com.xxl.rpc.netcom;

import com.xxl.rpc.netcom.common.NetComEnum;
import com.xxl.rpc.netcom.common.client.IClient;
import com.xxl.rpc.netcom.common.codec.RpcRequest;
import com.xxl.rpc.netcom.common.codec.RpcResponse;
import com.xxl.rpc.serialize.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * rpc proxy
 * @author xuxueli 2015-10-29 20:18:32
 */
public class NetComClientProxy implements FactoryBean<Object>, InitializingBean {
	private static final Logger logger = LoggerFactory.getLogger(NetComClientProxy.class);	 
	// [tips01: save 30ms/100invoke. why why why??? with this logger, it can save lots of time.]

	// ---------------------- config ----------------------
	private String serverAddress;
	private NetComEnum netcom = NetComEnum.NETTY;
	private Serializer serializer = Serializer.SerializeEnum.HESSIAN.serializer;
	private Class<?> iface;
	private long timeoutMillis = 5000;
	private String accessToken;
	
	public NetComClientProxy(){	}
	public NetComClientProxy(String serverAddress, NetComEnum netcom, Serializer serializer, Class<?> iface, long timeoutMillis, String accessToken) {
		this.setServerAddress(serverAddress);
		this.netcom = netcom;
		this.serializer = serializer;
		this.setIface(iface);
		this.setTimeoutMillis(timeoutMillis);
		this.setAccessToken(accessToken);
		try {
			this.afterPropertiesSet();
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 *	public static <T> ClientProxy ClientProxy<T> getFuture(Class<T> type) {
	 *		<T> ClientProxy proxy = (<T>) new ClientProxy();
	 *		return proxy;
	 *	}
	 */

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	public void setNetcom(String netcom) {
		this.netcom = NetComEnum.autoMatch(netcom, NetComEnum.NETTY);
	}
	public void setSerializer(String serializer) {
		this.serializer = Serializer.SerializeEnum.match(serializer, Serializer.SerializeEnum.HESSIAN).serializer;
	}
	public void setIface(Class<?> iface) {
		this.iface = iface;
	}
	public void setTimeoutMillis(long timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	// ---------------------- init client, operate ----------------------
	IClient client = null;
	@Override
	public void afterPropertiesSet() throws Exception {
		client = netcom.clientClass.newInstance();
		client.init(serverAddress, serializer, timeoutMillis);
	}

	@Override
	public Object getObject() throws Exception {
		return Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), new Class[] { iface },
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						
						// request
						RpcRequest request = new RpcRequest();
	                    request.setRequestId(UUID.randomUUID().toString());
	                    request.setCreateMillisTime(System.currentTimeMillis());
	                    request.setAccessToken(accessToken);
	                    request.setClassName(method.getDeclaringClass().getName());
	                    request.setMethodName(method.getName());
	                    request.setParameterTypes(method.getParameterTypes());
	                    request.setParameters(args);
	                    
	                    // send
	                    RpcResponse response = null;
	                    try {
							response = client.send(request);
						} catch (Throwable throwable) {
							response = new RpcResponse();
							response.setError(throwable);
						}
	                    
	                    // valid response
						if (response == null) {
							logger.error(">>>>>>>>>>> xxl-rpc netty response not found.");
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
