package com.xxl.rpc.netcom.mina.client;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.rpc.netcom.common.codec.RpcCallbackFuture;
import com.xxl.rpc.netcom.common.codec.RpcResponse;

/**
 * rpc mina handler
 * @author xuxueli 2015-11-14 18:55:19
 */
public class MinaClientHandler extends IoHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(MinaClientHandler.class);

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		super.exceptionCaught(session, cause);
		logger.error(">>>>>>>>>>> xxl-rpc mina client caught exception:", cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		RpcResponse response = (RpcResponse) message;
		RpcCallbackFuture future = RpcCallbackFuture.futurePool.get(response.getRequestId());
		future.setResponse(response);
		RpcCallbackFuture.futurePool.put(response.getRequestId(), future);
	}

}
