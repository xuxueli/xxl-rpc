package com.xxl.rpc.remoting.net.impl.netty_http.client;

import com.xxl.rpc.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.pool.ClientPooled;
import com.xxl.rpc.serialize.Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.URI;

public class NettyHttpPooledClient extends ClientPooled  {

    private EventLoopGroup group;
    private Channel channel;

    private Serializer serializer;
    private String address;
    private String host;

    @Override
    public void init(String host, int port, final Serializer serializer, final XxlRpcInvokerFactory xxlRpcInvokerFactory) throws Exception {
        this.group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new HttpResponseDecoder())
                                .addLast(new HttpRequestEncoder())
                                .addLast(new NettyHttpClientHandler(xxlRpcInvokerFactory, serializer));
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, true);
        this.channel = bootstrap.connect(host, port).sync().channel();

        this.serializer = serializer;
        this.address = "http://"+host+":"+port;
        this.host = host;

        // valid
        if (!isValidate()) {
            close();
            return;
        }

        logger.debug(">>>>>>>>>>> xxl-rpc netty client proxy, connect to server success at host:{}, port:{}", host, port);
    }


    @Override
    public boolean isValidate() {
        if (this.channel != null) {
            return this.channel.isActive();
        }
        return false;
    }


    @Override
    public void close() {
        if (this.channel!=null && this.channel.isActive()) {
            this.channel.close();		// if this.channel.isOpen()
        }
        if (this.group!=null && !this.group.isShutdown()) {
            this.group.shutdownGracefully();
        }
        logger.debug(">>>>>>>>>>> xxl-rpc netty client close.");
    }


    @Override
    public void send(XxlRpcRequest xxlRpcRequest) throws Exception {
        byte[] requestBytes = serializer.serialize(xxlRpcRequest);

        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, new URI(address).toASCIIString(), Unpooled.wrappedBuffer(requestBytes));
        request.headers().set(HttpHeaderNames.HOST, host);
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderNames.CONNECTION);
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());

        this.channel.writeAndFlush(request).sync();
    }

}
