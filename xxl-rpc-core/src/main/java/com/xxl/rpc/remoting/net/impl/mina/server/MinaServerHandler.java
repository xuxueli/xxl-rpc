package com.xxl.rpc.remoting.net.impl.mina.server;

import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import com.xxl.rpc.util.ThrowableUtil;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * mina server handler
 *
 * @author xuxueli
 *
 */
public class MinaServerHandler extends IoHandlerAdapter {
	private static Logger logger = LoggerFactory.getLogger(MinaServerHandler.class);


	private XxlRpcProviderFactory xxlRpcProviderFactory;
	private ThreadPoolExecutor serverHandlerPool;


	public MinaServerHandler(final XxlRpcProviderFactory xxlRpcProviderFactory, final ThreadPoolExecutor serverHandlerPool) {
		this.xxlRpcProviderFactory = xxlRpcProviderFactory;
		this.serverHandlerPool = serverHandlerPool;
	}

	@Override
	public void messageReceived(final IoSession session, Object message) throws Exception {

		// request
		final XxlRpcRequest xxlRpcRequest = (XxlRpcRequest) message;

		try {
			// do invoke
			serverHandlerPool.execute(new Runnable() {
				@Override
				public void run() {
					// invoke + response
					XxlRpcResponse xxlRpcResponse = xxlRpcProviderFactory.invokeService(xxlRpcRequest);

					session.write(xxlRpcResponse);
				}
			});
		} catch (Exception e) {
			// catch error
			XxlRpcResponse xxlRpcResponse = new XxlRpcResponse();
			xxlRpcResponse.setRequestId(xxlRpcRequest.getRequestId());
			xxlRpcResponse.setErrorMsg(ThrowableUtil.toString(e));

			session.write(xxlRpcResponse);
		}

	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.error(">>>>>>>>>>> xxl-rpc provider mina server caught exception", cause);
		session.closeOnFlush();
	}
}
