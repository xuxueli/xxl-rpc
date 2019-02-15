package com.xxl.rpc.remoting.net.impl.netty_http.server;

import com.xxl.rpc.remoting.net.impl.netty.server.NettyServerHandler;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import com.xxl.rpc.util.ThrowableUtil;
import com.xxl.rpc.util.XxlRpcException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class NettyHttpServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);


    private XxlRpcProviderFactory xxlRpcProviderFactory;
    private ThreadPoolExecutor serverHandlerPool;

    public NettyHttpServerHandler(final XxlRpcProviderFactory xxlRpcProviderFactory, final ThreadPoolExecutor serverHandlerPool) {
        this.xxlRpcProviderFactory = xxlRpcProviderFactory;
        this.serverHandlerPool = serverHandlerPool;
    }

    private HttpRequest request;
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            request = (HttpRequest) msg;
        }
        if (msg instanceof HttpContent) {
            final HttpContent content = (HttpContent) msg;

            // do invoke
            serverHandlerPool.execute(new Runnable() {
                @Override
                public void run() {
                    process(ctx, request, content);
                }
            });
        }
    }

    private void process(ChannelHandlerContext ctx, HttpRequest request, HttpContent content){
        String uri = request.uri();
        String requestId = null;
        try {
            if ("/services".equals(uri)) {	// services mapping

                // request
                StringBuffer stringBuffer = new StringBuffer("<ui>");
                for (String serviceKey: xxlRpcProviderFactory.getServiceData().keySet()) {
                    stringBuffer.append("<li>").append(serviceKey).append(": ").append(xxlRpcProviderFactory.getServiceData().get(serviceKey)).append("</li>");
                }
                stringBuffer.append("</ui>");

                // response serialize
                byte[] responseBytes = stringBuffer.toString().getBytes("UTF-8");

                // response-write
                writeResponse(ctx, request, responseBytes);

            } else {

                // request parse
                ByteBuf byteBuf = content.content();
                //String requestStr = byteBuf.toString(io.netty.util.CharsetUtil.UTF_8);
                byte[] requestBytes = ByteBufUtil.getBytes(byteBuf);
                byteBuf.release();

                if (requestBytes.length == 0) {
                    throw new XxlRpcException("xxl-rpc request data empty.");
                }

                XxlRpcRequest xxlRpcRequest = (XxlRpcRequest) xxlRpcProviderFactory.getSerializer().deserialize(requestBytes, XxlRpcRequest.class);
                requestId = xxlRpcRequest.getRequestId();

                // invoke + response
                XxlRpcResponse xxlRpcResponse = xxlRpcProviderFactory.invokeService(xxlRpcRequest);

                // response serialize
                byte[] responseBytes = xxlRpcProviderFactory.getSerializer().serialize(xxlRpcResponse);

                // response-write
                writeResponse(ctx, request, responseBytes);
            }
        } catch (Exception e) {

            // response error
            XxlRpcResponse xxlRpcResponse = new XxlRpcResponse();
            xxlRpcResponse.setRequestId(requestId);
            xxlRpcResponse.setErrorMsg(ThrowableUtil.toString(e));

            // response serialize
            byte[] responseBytes = xxlRpcProviderFactory.getSerializer().serialize(xxlRpcResponse);

            // response-write
            writeResponse(ctx, request, responseBytes);
        }

    }

    /**
     * write response
     */
    private void writeResponse(ChannelHandlerContext ctx, HttpRequest request, byte[] responseBytes){
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(responseBytes));
        response.headers().set(CONTENT_TYPE, "text/html;charset=UTF-8");       // HttpHeaderValues.TEXT_PLAIN.toString()
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        if (HttpUtil.isKeepAlive(request)) {
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE.toString());
        }
        ctx.write(response);
        ctx.flush();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error(">>>>>>>>>>> xxl-rpc provider netty server caught exception", cause);
        ctx.close();
    }

}