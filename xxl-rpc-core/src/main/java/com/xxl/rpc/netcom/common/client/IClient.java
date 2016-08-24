package com.xxl.rpc.netcom.common.client;

import com.xxl.rpc.netcom.common.codec.RpcRequest;
import com.xxl.rpc.netcom.common.codec.RpcResponse;
import com.xxl.rpc.serialize.Serializer;

/**
 * i client
 * @author xuxueli 2015-11-24 22:18:10
 */
public abstract class IClient {
	
	// ---------------------- config ----------------------
	protected String serverAddress;
	protected Serializer serializer;
	protected long timeoutMillis;
	
	public void init(String serverAddress, Serializer serializer, long timeoutMillis) {
		this.serverAddress = serverAddress;
		this.serializer = serializer;
		this.timeoutMillis = timeoutMillis;
	}

    // ---------------------- operate ----------------------

	public abstract RpcResponse send(RpcRequest request) throws Exception;

}
