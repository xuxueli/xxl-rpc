package com.xxl.core.model;

import java.util.List;

/**
 * 服务信息
 * @author xuxueli 2016-3-19 16:34:05
 */
public class XxlRpcInfo {
	
	private String iface;
	private List<String> providers;
	public String getIface() {
		return iface;
	}
	public void setIface(String iface) {
		this.iface = iface;
	}
	public List<String> getProviders() {
		return providers;
	}
	public void setProviders(List<String> providers) {
		this.providers = providers;
	}
	
}
