package com.xxl.rpc.netcom.jetty.server;

import com.xxl.rpc.netcom.common.server.IServer;
import com.xxl.rpc.serialize.Serializer;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * rpc jetty server
 * @author xuxueli 2015-11-19 22:29:03
 *
 * 	<code>
		<!-- JETTY RPC, 服务端配置(类似Hessian B-RPC, +注册功能) -->
		<bean class="com.xxl.rpc.netcom.NetComServerFactory">
			<property name="port" value="7080" />
			<property name="netcom" value="JETTY" />
			<property name="serializer" value="HESSIAN" />
			<property name="zookeeper_switch" value="false" />
		</bean>
 * 	</code>
 */
public class JettyServer extends IServer {
	private static final Logger logger = LoggerFactory.getLogger(JettyServer.class);

	private Server server;
	private Thread thread;

	@Override
	public void start(final int port, final Serializer serializer) throws Exception {
		thread = new Thread(new Runnable() {
			@Override
			public void run() {

				// The Server
				server = new Server(new ExecutorThreadPool(1000));

				// HTTP connector
				ServerConnector connector = new ServerConnector(server);
				/*if (ip!=null && ip.trim().length()>0) {	// TODO, support set registry ip、and bind ip
					//connector.setHost(ip);	// The network interface this connector binds to as an IP address or a hostname.  If null or 0.0.0.0, then bind to all interfaces.
				}*/
				connector.setPort(port);
				server.setConnectors(new Connector[]{connector});

				// Set a handler
				HandlerCollection handlerc =new HandlerCollection();
				handlerc.setHandlers(new Handler[]{new JettyServerHandler(serializer)});
				server.setHandler(handlerc);

				try {
					server.start();
					logger.info(">>>>>>>>>>> xxl-rpc server start success, netcon={}, port={}", JettyServer.class.getName(), port);

					// TODO, registry move to here, shoud be later server init

					server.join();
				} catch (Exception e) {
					logger.error("", e);
				} finally {
					//server.destroy();
				}
			}
		});
		thread.setDaemon(true);	// daemon, service jvm, user thread leave >>> daemon leave >>> jvm leave
		thread.start();
	}

	@Override
	public void destroy() throws Exception {
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

		logger.info(">>>>>>>>>>> xxl-rpc server destroy success, netcon={}", JettyServer.class.getName());
	}
}
