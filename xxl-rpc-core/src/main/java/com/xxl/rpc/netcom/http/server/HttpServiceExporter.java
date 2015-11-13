package com.xxl.rpc.netcom.http.server;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import org.springframework.web.HttpRequestHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.util.NestedServletException;

import com.xxl.rpc.netcom.http.codec.HttpRequestInfo;
import com.xxl.rpc.netcom.http.codec.HttpResponseInfo;
import com.xxl.rpc.serialize.Serializer;
import com.xxl.rpc.util.HttpClientUtil;

/**
 * export spring.service as xxl-prc.service 
 * @author xuxueli 2015-9-29 14:35:21
 * 
 * 	<servlet>
		<servlet-name>xxl-rpc</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
		<load-on-startup>1</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>xxl-rpc</servlet-name>
		<url-pattern>/xxl-rpc/*</url-pattern>
	</servlet-mapping>
	
	<bean name="/demoService" class="com.xxl.rpc.netcom.http.server.HttpServiceExporter">
		<property name="iface" value="com.xxl.rpc.demo.service.IDemoService" />
		<property name="service" ref="demoService" />
		<property name="serialize" value="HESSIAN" />
	</bean>
	
 */
public class HttpServiceExporter implements HttpRequestHandler {
	
	private Class<?> iface;
	private Object service;
	private String serialize;
	public Class<?> getIface() {
		return iface;
	}
	public void setIface(Class<?> iface) {
		this.iface = iface;
	}
	public Object getService() {
		return service;
	}
	public void setService(Object service) {
		this.service = service;
	}
	public String getSerialize() {
		return serialize;
	}
	public void setSerialize(String serialize) {
		this.serialize = serialize;
	}
	
	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (!"POST".equals(request.getMethod())) {
			throw new HttpRequestMethodNotSupportedException(request.getMethod(),
					new String[] {"POST"}, "XxpRpcServiceExporter only supports POST requests");
		}
		if (iface == null) {
			throw new IllegalArgumentException("Property 'iface' is required");
		}
		if (service == null) {
			throw new IllegalArgumentException("Property 'service' is required");
		}
		if (!iface.isInstance(service)) {
			throw new IllegalArgumentException("Service interface [" + iface.getName() +
					"] needs to be implemented by service [" + service + "] of class [" +
					service.getClass().getName() + "]");
		}
		
		try {
			// serializer
	        Serializer serializer = Serializer.getInstance(serialize);
	        // serialize request
			byte[] requestBytes = HttpClientUtil.readBytes(request);
	        HttpRequestInfo httpRequest = (HttpRequestInfo) serializer.deserialize(requestBytes, HttpRequestInfo.class);
	        
			// invoke
	        HttpResponseInfo httpResponse = doInvoke(httpRequest);
	        byte[] responseBytes = serializer.serialize(httpResponse);
	        
			// response
			response.setCharacterEncoding("UTF-8");
			
			/*PrintWriter out = response.getWriter();
            out.print(responseBytes);
            out.flush();*/
			OutputStream out = response.getOutputStream();
			out.write(responseBytes);
			out.flush();
		} catch (Throwable ex) {
		  throw new NestedServletException(">>>>>>>>>>>> xx-rpc servlet deserialize exception.", ex);
		}
	}
	
	/**
	 * do invoke
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private HttpResponseInfo doInvoke(HttpRequestInfo request) throws Exception {
		HttpResponseInfo httpResponse = new HttpResponseInfo();
		httpResponse.setRequestId(request.getRequestId());
		
        Class<?> serviceClass = service.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        try {
        	/*Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            Object result = method.invoke(service, parameters);*/
            
			FastClass serviceFastClass = FastClass.create(serviceClass);
			FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
			Object result = serviceFastMethod.invoke(service, parameters);
			
			httpResponse.setResult(result);
		} catch (Exception e) {
			e.printStackTrace();
			httpResponse.setError(e);
		}
		return httpResponse;
	}
	
}
