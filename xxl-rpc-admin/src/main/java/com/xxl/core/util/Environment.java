package com.xxl.core.util;

import java.util.Properties;

/**
 * 环境基类
 * @author xuxueli 2015-8-28 10:37:43
 */
public class Environment {
	private static String environment_file = "/data/appcfg/xxl-cfg.properties";
	
	// zk地址：格式	ip1:port,ip2:port,ip3:port
	private static String zkserver;
	
	static {
		Properties prop = PropertiesUtil.loadProperties(environment_file, false);
		zkserver = PropertiesUtil.getString(prop, "zkserver", null);
	}
	
	public static String getZkserver() {
		return zkserver;
	}
	
	public static void main(String[] args) {
		System.out.println(getZkserver());
	}
}

