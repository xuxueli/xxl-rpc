package com.xxl.rpc.remoting.net.impl.netty.server;

import com.xxl.rpc.remoting.net.Server;
import com.xxl.rpc.remoting.net.impl.netty.codec.NettyDecoder;
import com.xxl.rpc.remoting.net.impl.netty.codec.NettyEncoder;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import com.xxl.rpc.util.XxlRpcException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

				// param
				final ThreadPoolExecutor serverHandlerPool = new ThreadPoolExecutor(
						60,
						300,
						60L,
						TimeUnit.SECONDS,
						new LinkedBlockingQueue<Runnable>(1000),
						new RejectedExecutionHandler() {
							@Override
							public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
								throw new XxlRpcException("xxl-rpc NettyServer Thread pool is EXHAUSTED!");
							}
						});		// default maxThreads 300, minThreads 60
				EventLoopGroup bossGroup = new NioEventLoopGroup();
				EventLoopGroup workerGroup = new NioEventLoopGroup();

				try {
					// start server
					ServerBootstrap bootstrap = new ServerBootstrap();
					bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
							.childHandler(new ChannelInitializer<SocketChannel>() {
								@Override
								public void initChannel(SocketChannel channel) throws Exception {
									channel.pipeline()
											.addLast(new NettyDecoder(XxlRpcRequest.class, xxlRpcProviderFactory.getSerializer()))
											.addLast(new NettyEncoder(XxlRpcResponse.class, xxlRpcProviderFactory.getSerializer()))
											.addLast(new NettyServerHandler(xxlRpcProviderFactory, serverHandlerPool));
								}
							})
							.option(ChannelOption.SO_TIMEOUT, 100)
							.option(ChannelOption.SO_BACKLOG, 128)
							.option(ChannelOption.TCP_NODELAY, true)
							.option(ChannelOption.SO_REUSEADDR, true)
							.childOption(ChannelOption.SO_KEEPALIVE, true);

					// bind
					ChannelFuture future = bootstrap.bind(xxlRpcProviderFactory.getPort()).sync();

                    logger.info(">>>>>>>>>>> xxl-rpc remoting server start success, nettype = {}, port = {}", NettyServer.class.getName(), xxlRpcProviderFactory.getPort());
					onStarted();

					// wait util stop
					future.channel().closeFuture().sync();

				} catch (Exception e) {
					if (e instanceof InterruptedException) {
						logger.info(">>>>>>>>>>> xxl-rpc remoting server stop.");
					} else {
						logger.error(">>>>>>>>>>> xxl-rpc remoting server error.", e);
					}
				} finally {

					// stop
					try {
						serverHandlerPool.shutdown();	// shutdownNow
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
					try {
						workerGroup.shutdownGracefully();
						bossGroup.shutdownGracefully();
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}

				}
			}
		});
		thread.setDaemon(true);
		thread.start();

	}

	@Override
	public void stop() throws Exception {

		// destroy server thread
        if (thread!=null && thread.isAlive()) {
            thread.interrupt();
        }

		// on stop
        onStoped();
        logger.info(">>>>>>>>>>> xxl-rpc remoting server destroy success.");
	}

}
