package com.xxl.rpc.netcom.mina.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.rpc.netcom.common.codec.RpcRequest;
import com.xxl.rpc.netcom.common.codec.RpcResponse;
import com.xxl.rpc.netcom.mina.codec.MinaDecoder;
import com.xxl.rpc.netcom.mina.codec.MinaEncoder;
import com.xxl.rpc.netcom.netty.server.NettyServer;
import com.xxl.rpc.registry.ZkServiceRegistry;
import com.xxl.rpc.serialize.Serializer;

/**
 * mina rpc server
 * 
 * @author xuxueli 2015-11-14 17:22:09
 */
public class MinaServer {
	private static final Logger logger = LoggerFactory
			.getLogger(NettyServer.class);

	private Map<String, Object> serviceMap;
	private int port;
	private Serializer serializer;
	boolean zookeeper_switch;

	public MinaServer(Map<String, Object> serviceMap, Serializer serializer, int port, boolean zookeeper_switch) {
		this.serviceMap = serviceMap;
		this.serializer = serializer;
		this.port = port;
		this.zookeeper_switch = zookeeper_switch;
	}

	public void start() throws Exception {
		new Thread(new Runnable() {
			@Override
			public void run() {
				NioSocketAcceptor acceptor = new NioSocketAcceptor();
				try {
					acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
					acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ProtocolCodecFactory() {
						@Override
						public ProtocolEncoder getEncoder(IoSession session) throws Exception {
							return new MinaEncoder(RpcResponse.class, serializer);
						}
						@Override
						public ProtocolDecoder getDecoder(IoSession session) throws Exception {
							return new MinaDecoder(RpcRequest.class, serializer);
						}
					}));
					acceptor.setHandler(new MinaServerHandler(serviceMap));
					
					SocketSessionConfig config = (SocketSessionConfig) acceptor.getSessionConfig();
					config.setReuseAddress(true);
					config.setTcpNoDelay(true);	// TCP_NODELAY和TCP_CORK基本上控制了包的“Nagle化”，这里我们主要讲TCP_NODELAY.Nagle化在这里的含义是采用Nagle算法把较小的包组装为更大的帧。
					config.setSoLinger(0);		// 执行Socket的close方法，该方法也会立即返回
					config.setReadBufferSize(1024 * 2);
					config.setIdleTime(IdleStatus.BOTH_IDLE, 10);
					
					acceptor.bind(new InetSocketAddress(port));
					if (zookeeper_switch) {
		            	ZkServiceRegistry.serviceRegistry.registerServices(port, serviceMap.keySet());
		            	logger.info(">>>>>>>>>>>> xxl-rpc mina provider registry service success, serviceMap:{}", serviceMap);
					}
					logger.info(">>>>>>>>>>> xxl-rpc mina server is running");
				} catch (IOException e) {
					logger.error(">>>>>>>>>>> xxl-rpc mina server fail", e);
					if (acceptor != null && acceptor.isActive()) {
						acceptor.unbind();
						acceptor.dispose();
					}
				}
			}
		}).start();

	}

}
