package com.xxl.rpc.remoting.net.impl.netty.server;

import com.xxl.rpc.remoting.net.Server;
import com.xxl.rpc.remoting.net.impl.netty.codec.NettyDecoder;
import com.xxl.rpc.remoting.net.impl.netty.codec.NettyEncoder;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * netty rpc server
 *
 * @author xuxueli 2015-10-29 18:17:14
 */
public class NettyServer extends Server {

	private Thread thread;

	@Override
	public void start(final XxlRpcProviderFactory xxlRpcProviderFactory) throws Exception {

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
											.addLast(new NettyDecoder(XxlRpcRequest.class, xxlRpcProviderFactory.getSerializer()))
											.addLast(new NettyEncoder(XxlRpcResponse.class, xxlRpcProviderFactory.getSerializer()))
											.addLast(new NettyServerHandler(xxlRpcProviderFactory));
								}
							})
							.option(ChannelOption.SO_TIMEOUT, 100)
							.option(ChannelOption.SO_BACKLOG, 128)
							.option(ChannelOption.TCP_NODELAY, true)
							.option(ChannelOption.SO_REUSEADDR, true)
							.childOption(ChannelOption.SO_KEEPALIVE, true);
					ChannelFuture future = bootstrap.bind(xxlRpcProviderFactory.getPort()).sync();

                    onStarted();
                    logger.info(">>>>>>>>>>> xxl-rpc remoting server start success, netcon={}, port={}", NettyServer.class.getName(), xxlRpcProviderFactory.getPort());

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
	public void stop() throws Exception {

        // destroy server
        if (thread!=null && thread.isAlive()) {
            thread.interrupt();
        }

        onStoped();
        logger.info(">>>>>>>>>>> xxl-rpc remoting server destroy success.");
	}

}
