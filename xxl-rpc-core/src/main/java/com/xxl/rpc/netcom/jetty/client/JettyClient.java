package com.xxl.rpc.netcom.jetty.client;

import com.xxl.rpc.netcom.common.client.IClient;
import com.xxl.rpc.netcom.common.codec.RpcRequest;
import com.xxl.rpc.netcom.common.codec.RpcResponse;
import com.xxl.rpc.registry.ZkServiceDiscovery;
import com.xxl.rpc.util.HttpClientUtil;

/**
 * jetty client
 * @author xuxueli 2015-11-24 22:25:15
 */
public class JettyClient extends IClient {

	@Override
	public RpcResponse send(RpcRequest request) throws Exception {
		
		if (zookeeper_switch) {
			serverAddress = ZkServiceDiscovery.zkServiceDiscovery.discover(request.getClassName());
		}
		
		byte[] requestBytes = serializer.serialize(request);
		byte[] responseBytes = HttpClientUtil.postRequest("http://" + serverAddress + "/", requestBytes);
		return (RpcResponse) serializer.deserialize(responseBytes, RpcResponse.class);
		
	}

}
