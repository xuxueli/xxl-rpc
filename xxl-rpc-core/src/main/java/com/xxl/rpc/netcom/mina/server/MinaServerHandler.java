package com.xxl.rpc.netcom.mina.server;

import com.xxl.rpc.netcom.NetComServerFactory;
import com.xxl.rpc.netcom.common.codec.RpcRequest;
import com.xxl.rpc.netcom.common.codec.RpcResponse;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息处理器
 * @author xuxueli
 *
 */
public class MinaServerHandler extends IoHandlerAdapter {
	private static Logger logger = LoggerFactory.getLogger(MinaServerHandler.class);
	

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		RpcRequest request = (RpcRequest) message;
		
		// invoke
        RpcResponse response = NetComServerFactory.invokeService(request, null);
        
        session.write(response);
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.error(">>>>>>>>>>> xxl-rpc provider mina server caught exception", cause);
		session.close(true);
	}
}
