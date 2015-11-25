package com.xxl.rpc.netcom.mina.server;

import java.util.Map;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.rpc.netcom.common.codec.RpcRequest;
import com.xxl.rpc.netcom.common.codec.RpcResponse;
import com.xxl.rpc.netcom.common.server.IRpcServiceInvoker;

/**
 * 消息处理器
 * @author xuxueli
 *
 */
public class MinaServerHandler extends IoHandlerAdapter {
	private static Logger logger = LoggerFactory.getLogger(MinaServerHandler.class);
	
    private final Map<String, Object> serviceMap;
    public MinaServerHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }
	
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		RpcRequest request = (RpcRequest) message;
		
		// invoke
		Object serviceBean = serviceMap.get(request.getClassName());
        RpcResponse response = IRpcServiceInvoker.invokeService(request, serviceBean);
        
        session.write(response);
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.error(">>>>>>>>>>> xxl-rpc provider mina server caught exception", cause);
		session.close(true);
	}
}
