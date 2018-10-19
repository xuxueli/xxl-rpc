package com.xxl.rpc.remoting.net.impl.jetty.server;

import com.xxl.rpc.remoting.net.Server;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;

/**
 * jetty server
 *
 * @author xuxueli 2015-11-19 22:29:03
 */
public class JettyServer extends Server {

	private org.eclipse.jetty.server.Server server;
	private Thread thread;


	@Override
	public void start(final XxlRpcProviderFactory xxlRpcProviderFactory) throws Exception {

		thread = new Thread(new Runnable() {

			@Override
			public void run() {

				// The Server
				server = new org.eclipse.jetty.server.Server(new ExecutorThreadPool(1000));

				// HTTP connector
				ServerConnector connector = new ServerConnector(server);
				/*if (ip!=null && ip.trim().length()>0) {	// TODO, support set registry ip、and bind ip
					//connector.setHost(ip);	// The network interface this connector binds to as an IP address or a hostname.  If null or 0.0.0.0, then bind to all interfaces.
				}*/
				connector.setPort(xxlRpcProviderFactory.getPort());
				server.setConnectors(new Connector[]{connector});

				// Set a handler
				HandlerCollection handlerc =new HandlerCollection();
				handlerc.setHandlers(new Handler[]{new JettyServerHandler(xxlRpcProviderFactory)});
				server.setHandler(handlerc);

				try {
					server.start();

					onStarted();
					logger.info(">>>>>>>>>>> xxl-rpc server start success, netcon={}, port={}", JettyServer.class.getName(), xxlRpcProviderFactory.getPort());

					server.join();
				} catch (Exception e) {
					logger.error(">>>>>>>>>>> xxl-rpc server start error.", e);
				} finally {
					try {
						server.stop();
						server.destroy();
					} catch (Exception e) {
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

		// destroy server
		if (server!=null && server.isRunning()) {
			try {
				server.stop();
				server.destroy();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		if (thread!=null && thread.isAlive()) {
			thread.interrupt();
		}

		onStoped();
		logger.info(">>>>>>>>>>> xxl-rpc server destroy success.");
	}
}
