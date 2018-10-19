package com.xxl.rpc.remoting.net;

import com.xxl.rpc.remoting.net.impl.jetty.client.JettyClient;
import com.xxl.rpc.remoting.net.impl.jetty.server.JettyServer;
import com.xxl.rpc.remoting.net.impl.mina.client.MinaClient;
import com.xxl.rpc.remoting.net.impl.mina.server.MinaServer;
import com.xxl.rpc.remoting.net.impl.netty.client.NettyClient;
import com.xxl.rpc.remoting.net.impl.netty.server.NettyServer;

/**
 * remoting net
 *
 * @author xuxueli 2015-11-24 22:09:57
 */
public enum NetEnum {


	NETTY(NettyServer.class, NettyClient.class, true),
	MINA(MinaServer.class, MinaClient.class, true),
	JETTY(JettyServer.class, JettyClient.class, false);


	public final Class<? extends Server> serverClass;
	public final Class<? extends Client> clientClass;
	public final boolean autoMatch;

	NetEnum(Class<? extends Server> serverClass, Class<? extends Client> clientClass, boolean prefer) {
		this.serverClass = serverClass;
		this.clientClass = clientClass;
		this.autoMatch = prefer;
	}

	public static NetEnum autoMatch(String name, NetEnum defaultEnum) {
		for (NetEnum item : NetEnum.values()) {
			if (item.autoMatch && item.name().equals(name)) {
				return item;
			}
		}
		return defaultEnum;
	}

}