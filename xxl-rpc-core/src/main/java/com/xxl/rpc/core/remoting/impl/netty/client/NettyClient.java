package com.xxl.rpc.core.remoting.impl.netty.client;

import com.xxl.rpc.core.boot.XxlRpcBootstrap;
import com.xxl.rpc.core.register.entity.RegisterInstance;
import com.xxl.rpc.core.remoting.Client;
import com.xxl.rpc.core.remoting.impl.netty.codec.NettyDecoder;
import com.xxl.rpc.core.remoting.impl.netty.codec.NettyEncoder;
import com.xxl.rpc.core.remoting.entity.XxlRpcBeat;
import com.xxl.rpc.core.remoting.entity.XxlRpcRequest;
import com.xxl.rpc.core.remoting.entity.XxlRpcResponse;
import com.xxl.rpc.core.serializer.Serializer;
import com.xxl.rpc.netty.shaded.io.netty.bootstrap.Bootstrap;
import com.xxl.rpc.netty.shaded.io.netty.channel.Channel;
import com.xxl.rpc.netty.shaded.io.netty.channel.ChannelInitializer;
import com.xxl.rpc.netty.shaded.io.netty.channel.ChannelOption;
import com.xxl.rpc.netty.shaded.io.netty.channel.nio.NioEventLoopGroup;
import com.xxl.rpc.netty.shaded.io.netty.channel.socket.SocketChannel;
import com.xxl.rpc.netty.shaded.io.netty.channel.socket.nio.NioSocketChannel;
import com.xxl.rpc.netty.shaded.io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * netty client
 *
 * @author xuxueli
 */
public class NettyClient extends Client {

    // client info
    private static NioEventLoopGroup nioEventLoopGroup;
    private Channel channel;

    // param
    private XxlRpcBootstrap rpcBootstrap;
    private RegisterInstance registerInstance;

    @Override
    public void init(final RegisterInstance registerInstance, final Serializer serializer, final XxlRpcBootstrap rpcBootstrap) throws Exception {
        // base param
        this.rpcBootstrap = rpcBootstrap;
        this.registerInstance = registerInstance;

        // lazy-init nioEventLoopGroup
        if (nioEventLoopGroup == null) {
            synchronized (NettyClient.class) {
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
        final NettyClient thisClient = this;
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new IdleStateHandler(0,0, XxlRpcBeat.BEAT_INTERVAL, TimeUnit.SECONDS))    // beat N, close if fail
                                .addLast(new NettyEncoder(XxlRpcRequest.class, serializer))
                                .addLast(new NettyDecoder(XxlRpcResponse.class, serializer))
                                .addLast(new NettyClientHandler(rpcBootstrap.getInvoker(), thisClient));
                    }
                })
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 1000);
        this.channel = bootstrap.connect(registerInstance.getIp(), registerInstance.getPort()).sync().channel();

        // valid
        if (!isValidate()) {
            close();
            return;
        }

        logger.info(">>>>>>>>>>> xxl-rpc NettyClient, connect to server success at host:{}, port:{}", registerInstance.getIp(), registerInstance.getPort());
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
        if (this.channel != null && this.channel.isActive()) {
            this.channel.close();        // if this.channel.isOpen()
        }
        // remove dead client
        this.rpcBootstrap.getInvoker().checkDeadAndRemoveClient(this.registerInstance.getUniqueKey());
        logger.info(">>>>>>>>>>> xxl-rpc NettyClient close, registerInstance#getUniqueKey = " + this.registerInstance.getUniqueKey());
    }

    @Override
    public void send(XxlRpcRequest xxlRpcRequest) throws Exception {
        // write (request) to server
        this.channel.writeAndFlush(xxlRpcRequest).sync();       // todo, change to async
    }

}
