package com.xxl.rpc.netcom.jetty.server;

import com.xxl.rpc.netcom.common.server.IServer;
import com.xxl.rpc.registry.ZkServiceRegistry;
import com.xxl.rpc.serialize.Serializer;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * rpc jetty server
 * @author xuxueli 2015-11-19 22:29:03
 */
public class JettyServer extends IServer {
	private static final Logger logger = LoggerFactory.getLogger(JettyServer.class);

	@Override
	public void start(final int port, final Serializer serializer, final Map<String, Object> serviceMap, final boolean zookeeper_switch) throws Exception {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Server server = new Server();
				server.setThreadPool(new ExecutorThreadPool(200, 200, 30000));	// 非阻塞
				
				// connector
				SelectChannelConnector connector = new SelectChannelConnector();
				connector.setPort(port);
				connector.setMaxIdleTime(30000);
				server.setConnectors(new Connector[] { connector });
				
				// handler
				HandlerCollection handlerc =new HandlerCollection();  
				handlerc.setHandlers(new Handler[]{new JettyServerHandler(serviceMap, serializer)});
				server.setHandler(handlerc);
				
				try {
					server.start();
					server.join();
					
					if (zookeeper_switch) {
		            	ZkServiceRegistry.registerServices(port, serviceMap.keySet());
		            	logger.info(">>>>>>>>>>>> xxl-rpc mina provider registry service success.");
					}
					logger.info(">>>>>>>>>>> xxl-rpc mina server started on port:{}, serviceMap:{}", port, serviceMap);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
