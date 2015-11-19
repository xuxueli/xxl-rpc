package com.xxl.rpc.netcom.jetty.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.xxl.rpc.netcom.common.codec.RpcRequest;
import com.xxl.rpc.netcom.common.codec.RpcResponse;
import com.xxl.rpc.serialize.Serializer;
import com.xxl.rpc.util.HttpClientUtil;

/**
 * jetty handler
 * @author xuxueli 2015-11-19 22:32:36
 */
public class JettyServerHandler extends AbstractHandler {
	
	private Map<String, Object> serviceMap;
	private Serializer serializer;
	public JettyServerHandler(Map<String, Object> serviceMap, Serializer serializer) {
		this.serviceMap = serviceMap;
		this.serializer = serializer;
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		// parse request
		byte[] requestBytes = HttpClientUtil.readBytes(request);
		RpcRequest requestTemp = (RpcRequest) serializer.deserialize(requestBytes, RpcRequest.class);
		
		// make response
		RpcResponse responseTemp = new RpcResponse();
		responseTemp.setRequestId(requestTemp.getRequestId());
        try {
            Object result = handle(requestTemp);
            responseTemp.setResult(result);
        } catch (Throwable t) {
        	responseTemp.setError(t);
        }
        byte[] responseBytes = serializer.serialize(responseTemp);
		
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		
		OutputStream out = response.getOutputStream();
		out.write(responseBytes);
		out.flush();
		
	}
	
	/**
     * jetty server handler request
     */
    private Object handle(RpcRequest request) throws Throwable {
        String className = request.getClassName();
        Object serviceBean = serviceMap.get(className);

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        /*Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(serviceBean, parameters);*/

        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceBean, parameters);
    }

}
