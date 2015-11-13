package com.xxl.rpc.netcom.http.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;

import com.xxl.rpc.netcom.http.codec.HttpRequestInfo;
import com.xxl.rpc.netcom.http.codec.HttpResponseInfo;
import com.xxl.rpc.serialize.Serializer;
import com.xxl.rpc.util.HttpClientUtil;

/**
 * xxl-prc easy rpc framework
 * @author xuxueli 2015-9-23 15:14:18
 * 
 * 	<bean id="demoService" class="com.xxl.rpc.netcom.http.client.HttpProxy">
		<property name="iface" value="com.xxl.rpc.demo.service.IDemoService" />
		<property name="serverAddress" value="http://localhost:8080/xxl-rpc-demo-server/xxl-rpc/demoService" />
		<property name="serialize" value="HESSIAN" />
	</bean>
	
	IDemoService hessianService = (IDemoService) new HttpProxy("http://localhost:8080/xxl-rpc-demo-server/xxl-rpc/demoService", IDemoService.class, "HESSIAN").getObject();
	System.out.println(hessianService.sayHi(new User("jack", "i miss you, lucy.")));
 */
public class HttpProxy implements FactoryBean<Object> {

	private String serverAddress;
	private Class<?> iface;
	private String serialize;
	public HttpProxy() {
		super();
	}
	public HttpProxy(String serverAddress, Class<?> iface, String serialize) {
		super();
		this.serverAddress = serverAddress;
		this.iface = iface;
		this.serialize = serialize;
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
	
	@Override
	public Object getObject() throws Exception {
		return Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), new Class[] { iface },
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						
						HttpRequestInfo httpRequest = new HttpRequestInfo();
						httpRequest.setIface(iface);
						httpRequest.setMethodName(method.getName());
						httpRequest.setParameterTypes(method.getParameterTypes());
						httpRequest.setParameters(args);

						Serializer serializer = Serializer.getInstance(serialize);
						byte[] requestBytes = serializer.serialize(httpRequest);
						
						// post request
						/*Map<String, String> params = new HashMap<String, String>();
						params.put("serialize", serializeType.name());*/
						
						byte[] responseBytes = HttpClientUtil.postRequest(serverAddress, requestBytes);
						HttpResponseInfo httpResponse = (HttpResponseInfo) serializer.deserialize(responseBytes, HttpResponseInfo.class);
						
						if (httpResponse.getError() != null) {
	                        throw httpResponse.getError();
	                    } else {
	                    	return httpResponse.getResult();
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
