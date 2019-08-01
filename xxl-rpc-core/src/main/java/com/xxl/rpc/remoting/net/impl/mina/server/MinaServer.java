package com.xxl.rpc.remoting.net.impl.mina.server;

import com.xxl.rpc.remoting.net.Server;
import com.xxl.rpc.remoting.net.impl.mina.codec.MinaDecoder;
import com.xxl.rpc.remoting.net.impl.mina.codec.MinaEncoder;
import com.xxl.rpc.remoting.net.impl.mina.keepalive.KeepAliveMessageFactoryImpl;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import com.xxl.rpc.util.ThreadPoolUtil;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * mina rpc server
 * 
 * @author xuxueli 2015-11-14 17:22:09
 */
public class MinaServer extends Server {

	private Thread thread;

	@Override
	public void start(final XxlRpcProviderFactory xxlRpcProviderFactory) throws Exception {

        thread = new Thread(new Runnable() {
			@Override
			public void run() {

				// param
				final ThreadPoolExecutor serverHandlerPool = ThreadPoolUtil.makeServerThreadPool(MinaServer.class.getSimpleName());
				NioSocketAcceptor acceptor = new NioSocketAcceptor();

				try {
					KeepAliveMessageFactory heartBeatFactory = new KeepAliveMessageFactoryImpl();
					//心跳超时直接关闭
					KeepAliveFilter heartBeat = new KeepAliveFilter(heartBeatFactory, IdleStatus.BOTH_IDLE, KeepAliveRequestTimeoutHandler.CLOSE);
					//是否回发
					heartBeat.setForwardEvent(true);
					//设置心跳频率
					heartBeat.setRequestInterval(5);
					heartBeat.setRequestTimeout(10);
					acceptor.getFilterChain().addLast("heartbeat", heartBeat);

					// start server
					acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
					acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ProtocolCodecFactory() {
						@Override
						public ProtocolEncoder getEncoder(IoSession session) throws Exception {
							return new MinaEncoder(XxlRpcResponse.class, xxlRpcProviderFactory.getSerializer());
						}
						@Override
						public ProtocolDecoder getDecoder(IoSession session) throws Exception {
							return new MinaDecoder(XxlRpcRequest.class, xxlRpcProviderFactory.getSerializer());
						}
					}));
					acceptor.setHandler(new MinaServerHandler(xxlRpcProviderFactory, serverHandlerPool));

					SocketSessionConfig config = acceptor.getSessionConfig();
					config.setTcpNoDelay(true);
					config.setKeepAlive(true);
					//config.setReuseAddress(true);
					config.setSoLinger(-1);
					config.setIdleTime(IdleStatus.BOTH_IDLE, 10);
					
					acceptor.bind(new InetSocketAddress(xxlRpcProviderFactory.getPort()));

					logger.info(">>>>>>>>>>> xxl-rpc remoting server start success, nettype = {}, port = {}", MinaServer.class.getName(), xxlRpcProviderFactory.getPort());
					onStarted();

					while (!Thread.currentThread().isInterrupted()) {
						Thread.sleep(1);
					}
				} catch (Exception e) {
					if (e instanceof InterruptedException) {
						logger.info(">>>>>>>>>>> xxl-rpc remoting server stop.");
					} else {
						logger.error(">>>>>>>>>>> xxl-rpc remoting server error.", e);
					}
				} finally {

					// stop
					try {
						serverHandlerPool.shutdown();
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
					try {
						if (acceptor.isActive()) {
							acceptor.unbind();
							acceptor.dispose();
						}
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
