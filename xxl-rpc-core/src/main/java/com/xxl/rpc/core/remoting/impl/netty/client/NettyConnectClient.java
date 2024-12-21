package com.xxl.rpc.core.remoting.impl.netty.client;

import com.xxl.rpc.core.factory.XxlRpcFactory;
import com.xxl.rpc.core.remoting.common.ConnectClient;
import com.xxl.rpc.core.remoting.impl.netty.codec.NettyDecoder;
import com.xxl.rpc.core.remoting.impl.netty.codec.NettyEncoder;
import com.xxl.rpc.core.remoting.params.XxlRpcBeat;
import com.xxl.rpc.core.remoting.params.XxlRpcRequest;
import com.xxl.rpc.core.remoting.params.XxlRpcResponse;
import com.xxl.rpc.core.serializer.Serializer;
import com.xxl.rpc.core.util.IpUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * netty pooled client
 *
 * @author xuxueli
 */
public class NettyConnectClient extends ConnectClient {
    private static NioEventLoopGroup nioEventLoopGroup;

    private Channel channel;


    @Override
    public void init(String address, final Serializer serializer, final XxlRpcFactory factory) throws Exception {
        // address
        Object[] array = IpUtil.parseIpPort(address);
        String host = (String) array[0];
        int port = (int) array[1];

        // group
        if (nioEventLoopGroup == null) {
            synchronized (NettyConnectClient.class) {
                if (nioEventLoopGroup == null) {
                    nioEventLoopGroup = new NioEventLoopGroup();
                    factory.addStopCallable(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            nioEventLoopGroup.shutdownGracefully();
                            return null;
                        }
                    });
                }
            }
        }

        // init
        final NettyConnectClient thisClient = this;
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
                                .addLast(new NettyClientHandler(factory.getInvoker(), thisClient));
                    }
                })
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
        this.channel = bootstrap.connect(host, port).sync().channel();

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
        if (this.channel != null && this.channel.isActive()) {
            this.channel.close();        // if this.channel.isOpen()
        }
        logger.debug(">>>>>>>>>>> xxl-rpc netty client close.");
    }


    @Override
    public void send(XxlRpcRequest xxlRpcRequest) throws Exception {
        this.channel.writeAndFlush(xxlRpcRequest).sync();
    }
}
