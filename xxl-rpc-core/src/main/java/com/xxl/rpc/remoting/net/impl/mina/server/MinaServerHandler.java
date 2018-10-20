package com.xxl.rpc.remoting.net.impl.mina.server;

import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mina server handler
 *
 * @author xuxueli
 *
 */
public class MinaServerHandler extends IoHandlerAdapter {
	private static Logger logger = LoggerFactory.getLogger(MinaServerHandler.class);


	private XxlRpcProviderFactory xxlRpcProviderFactory;
	public MinaServerHandler(XxlRpcProviderFactory xxlRpcProviderFactory) {
		this.xxlRpcProviderFactory = xxlRpcProviderFactory;
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {

		// request
		XxlRpcRequest xxlRpcRequest = (XxlRpcRequest) message;
		
		// invoke + response
        XxlRpcResponse xxlRpcResponse = xxlRpcProviderFactory.invokeService(xxlRpcRequest);
        
        session.write(xxlRpcResponse);
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {

		// error response, TODO


		logger.error(">>>>>>>>>>> xxl-rpc provider mina server caught exception", cause);
		session.closeNow();
	}
}
