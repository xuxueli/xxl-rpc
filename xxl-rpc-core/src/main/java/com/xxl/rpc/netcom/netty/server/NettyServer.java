package com.xxl.rpc.netcom.netty.server;

import com.xxl.rpc.netcom.common.codec.RpcRequest;
import com.xxl.rpc.netcom.common.codec.RpcResponse;
import com.xxl.rpc.netcom.common.server.IServer;
import com.xxl.rpc.netcom.netty.codec.NettyDecoder;
import com.xxl.rpc.netcom.netty.codec.NettyEncoder;
import com.xxl.rpc.registry.ZkServiceRegistry;
import com.xxl.rpc.serialize.Serializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * netty rpc server
 * @author xuxueli 2015-10-29 18:17:14
 */
public class NettyServer extends IServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    
    @Override
	public void start(final int port, final Serializer serializer, final Map<String, Object> serviceMap, final boolean zookeeper_switch) throws Exception {
    	new Thread(new Runnable() {
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
		                            .addLast(new NettyServerHandler(serviceMap));
		                    }
		                })
		                .option(ChannelOption.SO_BACKLOG, 128)
		                .option(ChannelOption.TCP_NODELAY, true)
		                .option(ChannelOption.SO_REUSEADDR, true)
		                .childOption(ChannelOption.SO_KEEPALIVE, true);
		            ChannelFuture future = bootstrap.bind(port).sync();
		            if (zookeeper_switch) {
		            	ZkServiceRegistry.registerServices(port, serviceMap.keySet());
		            	logger.info(">>>>>>>>>>>> xxl-rpc netty provider registry service success.");
					}
		            logger.info(">>>>>>>>>>> xxl-rpc netty server started on port:{}, serviceMap:{}", port, serviceMap);
		            future.channel().closeFuture().sync();
		        } catch (InterruptedException e) {
		        	logger.error(">>>>>>>>>>> xxl-rpc mina server fail.", e);
				} catch (KeeperException e) {
					logger.error(">>>>>>>>>>> xxl-rpc mina server fail.", e);
				} finally {
		            workerGroup.shutdownGracefully();
		            bossGroup.shutdownGracefully();
		        }
			}
		}).start();
    	
    }

}
