package com.xxl.rpc.core.remoting.impl.netty_http.server;

import com.xxl.rpc.core.boot.XxlRpcBootstrap;
import com.xxl.rpc.core.remoting.Server;
import com.xxl.rpc.core.remoting.entity.XxlRpcBeat;
import com.xxl.rpc.core.util.XxlRpcThreadPoolUtil;
import com.xxl.rpc.netty.shaded.io.netty.bootstrap.ServerBootstrap;
import com.xxl.rpc.netty.shaded.io.netty.channel.ChannelFuture;
import com.xxl.rpc.netty.shaded.io.netty.channel.ChannelInitializer;
import com.xxl.rpc.netty.shaded.io.netty.channel.ChannelOption;
import com.xxl.rpc.netty.shaded.io.netty.channel.EventLoopGroup;
import com.xxl.rpc.netty.shaded.io.netty.channel.nio.NioEventLoopGroup;
import com.xxl.rpc.netty.shaded.io.netty.channel.socket.SocketChannel;
import com.xxl.rpc.netty.shaded.io.netty.channel.socket.nio.NioServerSocketChannel;
import com.xxl.rpc.netty.shaded.io.netty.handler.codec.http.HttpObjectAggregator;
import com.xxl.rpc.netty.shaded.io.netty.handler.codec.http.HttpServerCodec;
import com.xxl.rpc.netty.shaded.io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * netty_http
 *
 * @author xuxueli 2015-11-24 22:25:15
 */
public class NettyHttpServer extends Server  {

    private Thread thread;

    @Override
    public void start(final XxlRpcBootstrap rpcBootstrap) throws Exception {

        thread = new Thread(new Runnable() {

            @Override
            public void run() {

                // param
                final ThreadPoolExecutor serverHandlerPool = XxlRpcThreadPoolUtil.makeServerThreadPool(
                        NettyHttpServer.class.getSimpleName(),
                        rpcBootstrap.getProviderConfig().getCorePoolSize(),
                        rpcBootstrap.getProviderConfig().getMaxPoolSize());
                EventLoopGroup bossGroup = new NioEventLoopGroup();
                EventLoopGroup workerGroup = new NioEventLoopGroup();

                try {
                    // start server
                    ServerBootstrap bootstrap = new ServerBootstrap();
                    bootstrap.group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                public void initChannel(SocketChannel channel) throws Exception {
                                    channel.pipeline()
                                            .addLast(new IdleStateHandler(0, 0, XxlRpcBeat.BEAT_INTERVAL * 3, TimeUnit.SECONDS))  // beat 3N, close if idle
                                            .addLast(new HttpServerCodec())
                                            .addLast(new HttpObjectAggregator(20 * 1024 * 1024))  // merge request & reponse to FULL
                                            .addLast(new NettyHttpServerHandler(rpcBootstrap.getProvider(), serverHandlerPool));
                                }
                            })
                            .childOption(ChannelOption.SO_KEEPALIVE, true);

                    // bind
                    ChannelFuture future = bootstrap.bind(rpcBootstrap.getProviderConfig().getPort()).sync();

                    logger.info(">>>>>>>>>>> xxl-rpc, NettyHttpServer start success, port = {}", rpcBootstrap.getProviderConfig().getPort());
                    onStarted();

                    // wait util stop
                    future.channel().closeFuture().sync();

                } catch (Throwable e) {
                    if (e instanceof InterruptedException) {
                        logger.info(">>>>>>>>>>> xxl-rpc, NettyHttpServer stop.");
                    } else {
                        logger.error(">>>>>>>>>>> xxl-rpc, NettyHttpServer error.", e);
                    }
                } finally {

                    // stop
                    try {
                        serverHandlerPool.shutdown();	// shutdownNow
                    } catch (Throwable e) {
                        logger.error(e.getMessage(), e);
                    }
                    try {
                        workerGroup.shutdownGracefully();
                        bossGroup.shutdownGracefully();
                    } catch (Throwable e) {
                        logger.error(e.getMessage(), e);
                    }
                }

            }

        });
        thread.setDaemon(true);	// daemon, service jvm, user thread leave >>> daemon leave >>> jvm leave
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
