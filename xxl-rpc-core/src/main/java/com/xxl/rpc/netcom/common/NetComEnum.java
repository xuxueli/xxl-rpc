package com.xxl.rpc.netcom.common;

import com.xxl.rpc.netcom.common.client.IClient;
import com.xxl.rpc.netcom.common.server.IServer;
import com.xxl.rpc.netcom.jetty.client.JettyClient;
import com.xxl.rpc.netcom.jetty.server.JettyServer;
import com.xxl.rpc.netcom.mina.client.MinaClient;
import com.xxl.rpc.netcom.mina.server.MinaServer;
import com.xxl.rpc.netcom.netty.client.NettyClient;
import com.xxl.rpc.netcom.netty.server.NettyServer;
import com.xxl.rpc.netcom.servlet.client.ServletClient;

/**
 * 通讯方案
 * @author xuxueli 2015-11-24 22:09:57
 */
public enum NetComEnum {

	NETTY(NettyServer.class, NettyClient.class), 
	MINA(MinaServer.class, MinaClient.class), 
	JETTY(JettyServer.class, JettyClient.class),
	SERVLET(null, ServletClient.class);

	public final Class<? extends IServer> serverClass;
	public final Class<? extends IClient> clientClass;

	private NetComEnum(Class<? extends IServer> serverClass,
			Class<? extends IClient> clientClass) {
		this.serverClass = serverClass;
		this.clientClass = clientClass;
	}

	public static NetComEnum match(String name, NetComEnum defaultEnum) {
		for (NetComEnum item : NetComEnum.values()) {
			if (item.name().equals(name)) {
				return item;
			}
		}
		return defaultEnum;
	}

}