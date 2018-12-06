package com.xxl.rpc.remoting.net.impl.mina.server;

import com.xxl.rpc.remoting.net.Server;
import com.xxl.rpc.remoting.net.impl.mina.codec.MinaDecoder;
import com.xxl.rpc.remoting.net.impl.mina.codec.MinaEncoder;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import com.xxl.rpc.util.XxlRpcException;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.net.InetSocketAddress;
import java.util.concurrent.*;

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
				final ThreadPoolExecutor serverHandlerPool = new ThreadPoolExecutor(
						60,
						300,
						60L,
						TimeUnit.SECONDS,
						new LinkedBlockingQueue<Runnable>(1000),
						new RejectedExecutionHandler() {
							@Override
							public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
								throw new XxlRpcException("xxl-rpc MinaServer Thread pool is EXHAUSTED!");
							}
						});		// default maxThreads 300, minThreads 60
				NioSocketAcceptor acceptor = new NioSocketAcceptor();

				try {
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
					config.setReuseAddress(true);
					config.setTcpNoDelay(true);	// TCP_NODELAY和TCP_CORK基本上控制了包的“Nagle化”，这里我们主要讲TCP_NODELAY.Nagle化在这里的含义是采用Nagle算法把较小的包组装为更大的帧。
					config.setSoLinger(0);		// 执行Socket的close方法，该方法也会立即返回
					config.setReadBufferSize(1024 * 2);
					config.setIdleTime(IdleStatus.BOTH_IDLE, 10);
					
					acceptor.bind(new InetSocketAddress(xxlRpcProviderFactory.getPort()));

					logger.info(">>>>>>>>>>> xxl-rpc remoting server start success, nettype = {}, port = {}", MinaServer.class.getName(), xxlRpcProviderFactory.getPort());
					onStarted();

				} catch (Exception e) {
					logger.error(">>>>>>>>>>> xxl-rpc remoting server start error.", e);
				} finally {

					// stop
					try {
						serverHandlerPool.shutdownNow();
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
					try {
						stop();
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
