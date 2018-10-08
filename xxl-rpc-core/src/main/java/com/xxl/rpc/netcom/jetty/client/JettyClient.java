package com.xxl.rpc.netcom.jetty.client;

import com.xxl.rpc.netcom.common.client.IClient;
import com.xxl.rpc.netcom.common.codec.RpcRequest;
import com.xxl.rpc.netcom.common.codec.RpcResponse;
import com.xxl.rpc.registry.ZkServiceDiscovery;
import com.xxl.rpc.util.HttpClientUtil;

/**
 * jetty client
 * @author xuxueli 2015-11-24 22:25:15
 *
 * 	<code>
		<!-- JETTY RPC, 客户端配置(类似Hessian B-RPC, +注册功能) -->
		<bean id="jettyDemoService" class="com.xxl.rpc.netcom.NetComClientProxy">
			<property name="netcom" value="JETTY" />
			<property name="serverAddress" value="127.0.0.1:7080" />
			<property name="serializer" value="HESSIAN" />
			<property name="iface" value="com.xxl.rpc.demo.api.IJettyDemoService" />
		</bean>
		或者:
		IJettyDemoService jettyService = (IJettyDemoService) new NetComClientProxy("127.0.0.1:7080", NetComEnum.JETTY.name(), "HESSIAN", IJettyDemoService.class, 1000 * 5).getObject();
 * 	</code>
 */
public class JettyClient extends IClient {

	@Override
	public RpcResponse send(RpcRequest request) throws Exception {
		
		if (serverAddress==null || serverAddress.trim().length()==0) {
			serverAddress = ZkServiceDiscovery.discover(request.getClassName());
		}

		// reqURL
		String reqURL = serverAddress;
		if (reqURL!=null && reqURL.toLowerCase().indexOf("http")==-1) {
			reqURL = "http://" + serverAddress + "/";	// IP:PORT, need parse to url
		}

		// serialize request
		byte[] requestBytes = serializer.serialize(request);

		// remote invoke
		byte[] responseBytes = HttpClientUtil.postRequest(reqURL, requestBytes);
		if (responseBytes == null || responseBytes.length==0) {
			RpcResponse rpcResponse = new RpcResponse();
			rpcResponse.setError(new RuntimeException("Network request fail, RpcResponse byte[] is null"));
			return rpcResponse;
		}

		// deserialize response
		return (RpcResponse) serializer.deserialize(responseBytes, RpcResponse.class);
		
	}

}
