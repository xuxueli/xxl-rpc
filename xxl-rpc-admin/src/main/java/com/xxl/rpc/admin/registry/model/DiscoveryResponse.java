package com.xxl.rpc.admin.registry.model;

import com.xxl.rpc.admin.model.dto.InstanceCacheDTO;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author xuxueli 2018-12-03
 */
public class DiscoveryResponse extends OpenApiResponse implements Serializable {
	public static final long serialVersionUID = 42L;

	/**
	 * Env
	 */
	private String env;

	/**
	 * discovery result data
	 *
	 * structure：Map
	 * 		key：appname
	 * 		value：List<RegisterInstance> = List ～ instance
	 *
	 */
	private Map<String, List<InstanceCacheDTO>> discoveryData;

	/**
	 * discovery result data-md5
	 *
	 * structure：Map
	 * 		key：appname
	 * 		value：md5
	 *
	 */
	private Map<String, String> discoveryDataMd5;

	public DiscoveryResponse(){}
	public DiscoveryResponse(int code, String msg) {
		super(code, msg);
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public Map<String, List<InstanceCacheDTO>> getDiscoveryData() {
		return discoveryData;
	}

	public void setDiscoveryData(Map<String, List<InstanceCacheDTO>> discoveryData) {
		this.discoveryData = discoveryData;
	}

	public Map<String, String> getDiscoveryDataMd5() {
		return discoveryDataMd5;
	}

	public void setDiscoveryDataMd5(Map<String, String> discoveryDataMd5) {
		this.discoveryDataMd5 = discoveryDataMd5;
	}

}