package com.xxl.rpc.remoting.net.impl.netty.client;

import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.rpc.remoting.net.params.RpcCallbackFuture;

/**
 * rpc netty client handler
 *
 * @author xuxueli 2015-10-31 18:00:27
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<XxlRpcResponse> {
	private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, XxlRpcResponse xxlRpcResponse) throws Exception {
		RpcCallbackFuture future = RpcCallbackFuture.futurePool.get(xxlRpcResponse.getRequestId());
		future.setXxlRpcResponse(xxlRpcResponse);
		RpcCallbackFuture.futurePool.put(xxlRpcResponse.getRequestId(), future);
    }
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	logger.error(">>>>>>>>>>> xxl-rpc netty client caught exception", cause);
        ctx.close();
    }

}
