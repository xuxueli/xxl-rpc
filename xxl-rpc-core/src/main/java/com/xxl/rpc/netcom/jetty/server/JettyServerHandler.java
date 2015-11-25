package com.xxl.rpc.netcom.jetty.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.xxl.rpc.netcom.common.codec.RpcRequest;
import com.xxl.rpc.netcom.common.codec.RpcResponse;
import com.xxl.rpc.netcom.common.server.IRpcServiceInvoker;
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
		
		// deserialize request
		byte[] requestBytes = HttpClientUtil.readBytes(request);
		RpcRequest rpcRequest = (RpcRequest) serializer.deserialize(requestBytes, RpcRequest.class);
		
		// invoke
        Object serviceBean = serviceMap.get(rpcRequest.getClassName());
        RpcResponse rpcResponse = IRpcServiceInvoker.invokeService(rpcRequest, serviceBean);
        
        // serialize response
        byte[] responseBytes = serializer.serialize(rpcResponse);
		
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		
		OutputStream out = response.getOutputStream();
		out.write(responseBytes);
		out.flush();
		
	}

}
