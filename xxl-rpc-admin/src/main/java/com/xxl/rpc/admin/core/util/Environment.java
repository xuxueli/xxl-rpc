package com.xxl.rpc.admin.core.util;

import java.util.Properties;

/**
 * 环境基类
 * @author xuxueli 2015-8-28 10:37:43
 */
public class Environment {


	/**
	 * rpc service address on zookeeper, servicePath : /xxl-rpc/interfaceName/serverAddress(ip01:port9999)
     */
	public static final String ZK_SERVICES_PATH = "/xxl-rpc";

	/**
	 * zk config file
	 */
	private static final String ZK_ADDRESS_FILE = "/data/webapps/xxl-rpc.properties";

	/**
	 * zk address
	 */
	public static final String ZK_ADDRESS;		// zk地址：格式	ip1:port,ip2:port,ip3:port
	
	static {
		Properties prop = PropertiesUtil.loadFileProperties(ZK_ADDRESS_FILE);
		ZK_ADDRESS = PropertiesUtil.getString(prop, "zkserver");
	}
	
	public static void main(String[] args) {
		System.out.println(ZK_ADDRESS);
	}

}

