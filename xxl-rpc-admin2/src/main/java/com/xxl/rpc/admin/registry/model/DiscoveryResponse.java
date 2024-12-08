package com.xxl.rpc.admin.registry.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author xuxueli 2018-12-03
 */
public class DiscoveryResponse extends OpenApiResponse implements Serializable {
	public static final long serialVersionUID = 42L;

	/**
	 * discovery result data
	 *
	 * structure：Map
	 * 		key：DiscoveryInstance = env + appname
	 * 		value：List<RegisterInstance> = List ～ instance
	 *
	 */
	private Map<DiscoveryInstance, List<RegisterInstance>> discoveryData;

	public DiscoveryResponse(){}
	public DiscoveryResponse(int code, String msg) {
		super(code, msg);
	}


}