package com.xxl.rpc.core.remoting.impl.netty_http.client;

import com.xxl.rpc.core.invoker.InvokerFactory;
import com.xxl.rpc.core.remoting.params.XxlRpcBeat;
import com.xxl.rpc.core.remoting.params.XxlRpcResponse;
import com.xxl.rpc.core.serializer.Serializer;
import com.xxl.rpc.core.util.XxlRpcException;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * netty_http
 *
 * @author xuxueli 2015-11-24 22:25:15
 */
public class NettyHttpClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    private static final Logger logger = LoggerFactory.getLogger(NettyHttpClientHandler.class);


    private InvokerFactory xxlRpcInvokerFactory;
    private Serializer serializer;
    private NettyHttpConnectClient nettyHttpConnectClient;
    public NettyHttpClientHandler(final InvokerFactory xxlRpcInvokerFactory, Serializer serializer, final NettyHttpConnectClient nettyHttpConnectClient) {
        this.xxlRpcInvokerFactory = xxlRpcInvokerFactory;
        this.serializer = serializer;
        this.nettyHttpConnectClient = nettyHttpConnectClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {

        // valid status
        if (!HttpResponseStatus.OK.equals(msg.status())) {
            throw new XxlRpcException("xxl-rpc response status invalid.");
        }

        HttpHeaders headers = msg.headers();
        String connection = headers.get("connection");
        if (Objects.equals("close", connection)) {
            logger.warn(">>>>>>>>>>> xxl-rpc netty_http client received close");
            ctx.close();
            return;
        }

        // response parse
        byte[] responseBytes = ByteBufUtil.getBytes(msg.content());

        // valid length
        if (responseBytes.length == 0) {
            throw new XxlRpcException("xxl-rpc response data empty.");
        }

        // response deserialize
        XxlRpcResponse xxlRpcResponse = (XxlRpcResponse) serializer.deserialize(responseBytes, XxlRpcResponse.class);

        // notify response
        xxlRpcInvokerFactory.notifyInvokerFuture(xxlRpcResponse.getRequestId(), xxlRpcResponse);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);
        logger.error(">>>>>>>>>>> xxl-rpc netty_http client caught exception", cause);
        ctx.close();
    }

    /*@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // retry
        super.channelInactive(ctx);
    }*/

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            /*ctx.channel().close();      // close idle channel
            logger.debug(">>>>>>>>>>> xxl-rpc netty_http client close an idle channel.");*/

            nettyHttpConnectClient.send(XxlRpcBeat.BEAT_PING);    // beat N, close if fail(may throw error)
            logger.debug(">>>>>>>>>>> xxl-rpc netty_http client send beat-ping.");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
