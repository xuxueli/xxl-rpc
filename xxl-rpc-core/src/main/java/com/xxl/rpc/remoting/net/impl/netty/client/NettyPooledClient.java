package com.xxl.rpc.remoting.net.impl.netty.client;

import com.xxl.rpc.remoting.net.impl.netty.codec.NettyDecoder;
import com.xxl.rpc.remoting.net.impl.netty.codec.NettyEncoder;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import com.xxl.rpc.remoting.net.pool.ClientPooled;
import com.xxl.rpc.serialize.Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * netty pooled client
 *
 * @author xuxueli
 */
public class NettyPooledClient extends ClientPooled  {


	private EventLoopGroup group;
	private Channel channel;


	@Override
	public void init(String host, int port, final Serializer serializer) throws Exception {
		this.group = new NioEventLoopGroup();
    	Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    channel.pipeline()
                        .addLast(new NettyEncoder(XxlRpcRequest.class, serializer))
                        .addLast(new NettyDecoder(XxlRpcResponse.class, serializer))
                        .addLast(new NettyClientHandler());
                }
            })
            .option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.SO_REUSEADDR, true)
            .option(ChannelOption.SO_KEEPALIVE, true);
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
		if (this.channel != null) {
			this.channel.close();		// if this.channel.isOpen()
			this.channel = null;
		}
		if (this.group != null) {
			this.group.shutdownGracefully();
			this.group = null;
		}
		logger.debug(">>>>>>>>>>> xxl-rpc netty client close.");
	}


	@Override
	public void send(XxlRpcRequest xxlRpcRequest) throws Exception {
    	this.channel.writeAndFlush(xxlRpcRequest).sync();
    }
}
