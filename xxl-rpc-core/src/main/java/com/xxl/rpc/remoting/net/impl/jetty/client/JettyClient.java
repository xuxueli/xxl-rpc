package com.xxl.rpc.remoting.net.impl.jetty.client;

import com.xxl.rpc.remoting.net.Client;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import com.xxl.rpc.util.HttpClientUtil;

//import com.xxl.rpc.registry.ZkServiceDiscovery;

/**
 * jetty client
 *
 * @author xuxueli 2015-11-24 22:25:15
 */
public class JettyClient extends Client {

	@Override
	public XxlRpcResponse send(String address, XxlRpcRequest xxlRpcRequest) throws Exception {

		// reqURL
		if (!address.toLowerCase().startsWith("http")) {
			address = "http://" + address + "/";	// IP:PORT, need parse to url
		}

		// serialize xxlRpcRequest
		byte[] requestBytes = xxlRpcReferenceBean.getSerializer().serialize(xxlRpcRequest);

		// remote invoke
		byte[] responseBytes = HttpClientUtil.postRequest(address, requestBytes, xxlRpcReferenceBean.getTimeout());
		if (responseBytes == null || responseBytes.length==0) {
			XxlRpcResponse xxlRpcResponse = new XxlRpcResponse();
			xxlRpcResponse.setError(new RuntimeException("Network xxlRpcRequest fail, XxlRpcResponse byte[] is null"));
			return xxlRpcResponse;
		}

		// deserialize response
		return (XxlRpcResponse) xxlRpcReferenceBean.getSerializer().deserialize(responseBytes, XxlRpcResponse.class);
		
	}

}
