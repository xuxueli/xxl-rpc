package com.xxl.rpc.netcom.common.server;

import java.util.Map;

import com.xxl.rpc.netcom.common.NetComEnum;
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
	 * 
	 * @param netcom_type		net-com type
	 * @param serviceMap		config
	 * @param port				config
	 * @param serialize			config
	 * @param zookeeper_switch	config
	 * @return
	 */
	public static IServer getInstance(String netcom_type, Map<String, Object> serviceMap, int port, String serialize, boolean zookeeper_switch) {
		NetComEnum netCom = NetComEnum.match(netcom_type, NetComEnum.NETTY);
		
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
