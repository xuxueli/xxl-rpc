package com.xxl.rpc.netcom.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.rpc.netcom.netty.codec.NettyDecoder;
import com.xxl.rpc.netcom.netty.codec.NettyEncoder;
import com.xxl.rpc.netcom.netty.codec.NettyRequest;
import com.xxl.rpc.netcom.netty.codec.NettyResponse;
import com.xxl.rpc.serialize.Serializer;

/**
 * connetion proxy
 * @author xuxueli
 */
public class NettyClientPoolProxy {
	private static transient Logger logger = LoggerFactory.getLogger(NettyClientPoolProxy.class);
	
	private Channel channel;
	public void createProxy(String host, int port, final Serializer serializer) throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
    	Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    channel.pipeline()
                        .addLast(new NettyEncoder(NettyRequest.class, serializer))
                        .addLast(new NettyDecoder(NettyResponse.class, serializer))
                        .addLast(new NettyClientHandler());
                }
            })
            .option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.SO_REUSEADDR, true)
            .option(ChannelOption.SO_KEEPALIVE, true);
        this.channel = bootstrap.connect(host, port).sync().channel();
	}
	
	public Channel getChannel() {
		return this.channel;
	}
	
	public boolean isValidate() {
		if (this.channel != null) {
			return this.channel.isActive();
		}
		return false;
	}
	
	public void close() {
		if (this.channel != null) {
			if (this.channel.isOpen()) {
				this.channel.close();
			}
		}
		logger.info(">>>>>>>>> netty channel close.");
	}
	
	public void send(NettyRequest request) throws Exception {
    	this.channel.writeAndFlush(request).sync();
    }
}
