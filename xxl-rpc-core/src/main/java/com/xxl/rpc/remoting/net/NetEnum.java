package com.xxl.rpc.remoting.net;

import com.xxl.rpc.remoting.net.impl.jetty.client.JettyClient;
import com.xxl.rpc.remoting.net.impl.jetty.server.JettyServer;
import com.xxl.rpc.remoting.net.impl.mina.client.MinaClient;
import com.xxl.rpc.remoting.net.impl.mina.server.MinaServer;
import com.xxl.rpc.remoting.net.impl.netty.client.NettyClient;
import com.xxl.rpc.remoting.net.impl.netty.server.NettyServer;
import com.xxl.rpc.remoting.net.impl.netty_http.client.NettyHttpClient;
import com.xxl.rpc.remoting.net.impl.netty_http.server.NettyHttpServer;
import com.xxl.rpc.remoting.net.impl.servlet.server.ServletServer;

/**
 * remoting net
 *
 * @author xuxueli 2015-11-24 22:09:57
 */
public enum NetEnum {


	/**
	 * netty tcp server
	 */
	NETTY(NettyServer.class, NettyClient.class),

	/**
	 * netty http server
	 */
	NETTY_HTTP(NettyHttpServer.class, NettyHttpClient.class),

	/**
	 * servlet http, no server
	 */
    SERVLET_HTTP(ServletServer.class, NettyHttpClient.class),


	/**
	 * mina tcp server
	 */
	MINA(MinaServer.class, MinaClient.class),

	/**
	 * jetty http server
	 */
	JETTY(JettyServer .class, JettyClient .class);


	public final Class<? extends Server> serverClass;
	public final Class<? extends Client> clientClass;

	NetEnum(Class<? extends Server> serverClass, Class<? extends Client> clientClass) {
		this.serverClass = serverClass;
		this.clientClass = clientClass;
	}

	public static NetEnum autoMatch(String name, NetEnum defaultEnum) {
		for (NetEnum item : NetEnum.values()) {
			if (item.name().equals(name)) {
				return item;
			}
		}
		return defaultEnum;
	}

}