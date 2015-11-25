package com.xxl.rpc.netcom.servlet.client;

import com.xxl.rpc.netcom.common.client.IClient;
import com.xxl.rpc.netcom.common.codec.RpcRequest;
import com.xxl.rpc.netcom.common.codec.RpcResponse;
import com.xxl.rpc.util.HttpClientUtil;

/**
 * servlet client
 * @author xuxueli 2015-11-25 11:26:40
 */
public class ServletClient extends IClient {

	@Override
	public RpcResponse send(RpcRequest request) throws Exception {
		
		byte[] requestBytes = serializer.serialize(request);
		byte[] responseBytes = HttpClientUtil.postRequest(serverAddress, requestBytes);
		return (RpcResponse) serializer.deserialize(responseBytes, RpcResponse.class);
		
	}

}
