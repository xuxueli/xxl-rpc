package com.xxl.rpc.netcom.netty.server;

import com.xxl.rpc.netcom.common.codec.RpcRequest;
import com.xxl.rpc.netcom.common.codec.RpcResponse;
import com.xxl.rpc.netcom.common.server.IServer;
import com.xxl.rpc.netcom.netty.codec.NettyDecoder;
import com.xxl.rpc.netcom.netty.codec.NettyEncoder;
import com.xxl.rpc.serialize.Serializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * netty rpc server
 * @author xuxueli 2015-10-29 18:17:14
 */
public class NettyServer extends IServer {
	private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

	private Thread thread;

	@Override
	public void start(final int port, final Serializer serializer) throws Exception {
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				EventLoopGroup bossGroup = new NioEventLoopGroup();
				EventLoopGroup workerGroup = new NioEventLoopGroup();
				try {
					ServerBootstrap bootstrap = new ServerBootstrap();
					bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
							.childHandler(new ChannelInitializer<SocketChannel>() {
								@Override
								public void initChannel(SocketChannel channel) throws Exception {
									channel.pipeline()
											.addLast(new NettyDecoder(RpcRequest.class, serializer))
											.addLast(new NettyEncoder(RpcResponse.class, serializer))
											.addLast(new NettyServerHandler());
								}
							})
							.option(ChannelOption.SO_TIMEOUT, 100)
							.option(ChannelOption.SO_BACKLOG, 128)
							.option(ChannelOption.TCP_NODELAY, true)
							.option(ChannelOption.SO_REUSEADDR, true)
							.childOption(ChannelOption.SO_KEEPALIVE, true);
					ChannelFuture future = bootstrap.bind(port).sync();
					logger.info(">>>>>>>>>>> xxl-rpc server start success, netcon={}, port={}", NettyServer.class.getName(), port);
					Channel serviceChannel = future.channel().closeFuture().sync().channel();
				} catch (InterruptedException e) {
					logger.error("", e);
				} finally {
					workerGroup.shutdownGracefully();
					bossGroup.shutdownGracefully();
				}
			}
		});
		thread.setDaemon(true);
		thread.start();

	}

	@Override
	public void destroy() throws Exception {
		thread.interrupt();
		logger.info(">>>>>>>>>>> xxl-rpc server destroy success, netcon={}", NettyServer.class.getName());
	}

}
