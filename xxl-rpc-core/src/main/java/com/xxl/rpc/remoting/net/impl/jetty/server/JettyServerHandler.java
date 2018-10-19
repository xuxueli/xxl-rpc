package com.xxl.rpc.remoting.net.impl.jetty.server;

import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import com.xxl.rpc.util.HttpClientUtil;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * jetty handler
 * @author xuxueli 2015-11-19 22:32:36
 */
public class JettyServerHandler extends AbstractHandler {
	private static Logger logger = LoggerFactory.getLogger(JettyServerHandler.class);


	private XxlRpcProviderFactory xxlRpcProviderFactory;
	public JettyServerHandler(XxlRpcProviderFactory xxlRpcProviderFactory) {
		this.xxlRpcProviderFactory = xxlRpcProviderFactory;
	}


	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		// invoke
		XxlRpcResponse rpcXxlRpcResponse = doInvoke(request);

        // serialize response
        byte[] responseBytes = xxlRpcProviderFactory.getSerializer().serialize(rpcXxlRpcResponse);
		
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		
		OutputStream out = response.getOutputStream();
		out.write(responseBytes);
		out.flush();
		
	}

	private XxlRpcResponse doInvoke(HttpServletRequest request) {
		try {
			// deserialize request
			byte[] requestBytes = HttpClientUtil.readBytes(request);
			if (requestBytes == null || requestBytes.length==0) {
				throw new RuntimeException("XxlRpcRequest byte[] is null");
			}
			XxlRpcRequest rpcXxlRpcRequest = (XxlRpcRequest) xxlRpcProviderFactory.getSerializer().deserialize(requestBytes, XxlRpcRequest.class);

			// invoke
			XxlRpcResponse xxlRpcResponse = xxlRpcProviderFactory.invokeService(rpcXxlRpcRequest);
			return xxlRpcResponse;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);

			XxlRpcResponse xxlRpcResponse = new XxlRpcResponse();
			xxlRpcResponse.setError(e);
			return xxlRpcResponse;
		}
	}

}
