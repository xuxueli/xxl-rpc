package com.xxl.rpc.core.remoting.impl.netty_http.client;

import com.xxl.rpc.core.boot.XxlRpcBootstrap;
import com.xxl.rpc.core.register.entity.RegisterInstance;
import com.xxl.rpc.core.remoting.Client;
import com.xxl.rpc.core.remoting.entity.XxlRpcBeat;
import com.xxl.rpc.core.remoting.entity.XxlRpcRequest;
import com.xxl.rpc.core.serializer.Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * netty_http client
 *
 * @author xuxueli 2015-11-24 22:25:15
 */
public class NettyHttpClient extends Client {

    // client info
    private static NioEventLoopGroup nioEventLoopGroup;
    private Channel channel;

    // param
    private XxlRpcBootstrap rpcBootstrap;
    private Serializer serializer;
    private RegisterInstance registerInstance;

    @Override
    public void init(RegisterInstance registerInstance, final Serializer serializer, final XxlRpcBootstrap rpcBootstrap) throws Exception {
        // base param
        this.serializer = serializer;
        this.rpcBootstrap = rpcBootstrap;
        this.registerInstance = registerInstance;

        // lazy-init nioEventLoopGroup
        if (nioEventLoopGroup == null) {
            synchronized (NettyHttpClient.class) {
                if (nioEventLoopGroup == null) {
                    nioEventLoopGroup = new NioEventLoopGroup();
                    rpcBootstrap.addStopCallable(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            nioEventLoopGroup.shutdownGracefully();
                            return null;
                        }
                    });
                }
            }
        }

        // init client
        final NettyHttpClient thisClient = this;
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new IdleStateHandler(0,0, XxlRpcBeat.BEAT_INTERVAL, TimeUnit.SECONDS))   // beat N, close if fail
                                .addLast(new HttpClientCodec())
                                .addLast(new HttpObjectAggregator(5*1024*1024))
                                .addLast(new NettyHttpClientHandler(rpcBootstrap.getInvoker(), serializer, thisClient));
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
        this.channel = bootstrap.connect(registerInstance.getIp(), registerInstance.getPort()).sync().channel();



        // valid
        if (!isValidate()) {
            close();
            return;
        }

        logger.info(">>>>>>>>>>> xxl-rpc NettyHttpClient, connect to server success at host:{}, port:{}", registerInstance.getIp(), registerInstance.getPort());
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
        // close channel
        if (this.channel!=null && this.channel.isActive()) {
            this.channel.close();		// if this.channel.isOpen()
        }
        // remove dead client
        this.rpcBootstrap.getInvoker().checkDeadAndRemoveClient(this.registerInstance.getUniqueKey());
        logger.info(">>>>>>>>>>> xxl-rpc NettyHttpClient close, registerInstance#getUniqueKey = " + this.registerInstance.getUniqueKey());
    }


    @Override
    public void send(XxlRpcRequest xxlRpcRequest) throws Exception {
        // serialize
        byte[] requestBytes = serializer.serialize(xxlRpcRequest);

        // build request
        String uriStr = "http://" + this.registerInstance.getIp() + ":" + this.registerInstance.getPort();
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.POST,
                new URI(uriStr).getRawPath(),
                Unpooled.wrappedBuffer(requestBytes));

        request.headers().set(HttpHeaderNames.HOST, this.registerInstance.getIp());
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());

        // write (request) to server
        this.channel.writeAndFlush(request).sync();
    }

}
