package com.xxl.rpc.netcom.common.server;

import java.util.Map;

import com.xxl.rpc.netcom.jetty.server.JettyServer;
import com.xxl.rpc.netcom.mina.server.MinaServer;
import com.xxl.rpc.netcom.netty.server.NettyServer;
import com.xxl.rpc.serialize.Serializer;

/**
 * i server
 * @author xuxueli 2015-11-24 20:59:49
 */
public abstract class IServer {
	
	// init config
	protected Map<String, Object> serviceMap;
	protected Serializer serializer;
	protected int port;
	protected boolean zookeeper_switch;
	
    public void initConfig(Map<String, Object> serviceMap, String serialize, int port, boolean zookeeper_switch) {
    	this.serviceMap = serviceMap;
    	this.serializer = Serializer.getInstance(serialize);
		this.port = port;
		this.zookeeper_switch = zookeeper_switch;
	}
    
	public abstract void start() throws Exception;

	/**
	 * 通讯方案
	 */
	public enum NetComEnum{
		NETTY(NettyServer.class), 
		MINA(MinaServer.class), 
		JETTY(JettyServer.class);
		public final Class<? extends IServer> serverClass;
		private NetComEnum(Class<? extends IServer> serverClass){
			this.serverClass = serverClass;
		}
		public static NetComEnum match(String name){
			for (NetComEnum item : NetComEnum.values()) {
				if (item.name().equals(name)) {
					return item;
				}
			}
			return null;
		}
	}
	
	/**
	 * 
	 * @param netcom_type		net-com type
	 * @param serviceMap		config
	 * @param port				config
	 * @param serialize			config
	 * @param zookeeper_switch	config
	 * @return
	 */
	public static IServer getInstance(String netcom_type, Map<String, Object> serviceMap, int port, String serialize, boolean zookeeper_switch) {
		NetComEnum netCom = NetComEnum.match(netcom_type);
		if (netCom == null) {
			netCom = NetComEnum.NETTY;
		}
		
		IServer server = null;
		try {
			server = netCom.serverClass.newInstance();
			server.initConfig(serviceMap, serialize, port, zookeeper_switch);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return server;
	}
	
}
