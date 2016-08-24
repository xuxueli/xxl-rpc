package com.xxl.rpc.netcom.servlet.client;

import com.xxl.rpc.netcom.common.client.IClient;
import com.xxl.rpc.netcom.common.codec.RpcRequest;
import com.xxl.rpc.netcom.common.codec.RpcResponse;
import com.xxl.rpc.util.HttpClientUtil;

/**
 * servlet client
 * @author xuxueli 2015-11-25 11:26:40
 *
 * <code>
		<!-- SERVLET RPC, 客户端配置(类似Hessian B-RPC) -->
		<bean id="servletDemoService" class="com.xxl.rpc.netcom.NetComClientProxy">
			<property name="netcom_type" value="SERVLET" />
			<property name="iface" value="com.xxl.rpc.demo.api.IServletDemoService" />
			<property name="serverAddress" value="http://127.0.0.1:8080/xxl-rpc-demo-server/xxl-rpc/demoService" />
			<property name="serialize" value="HESSIAN" />
		</bean>
 * </code>
 *
 */
public class ServletClient extends IClient {

	@Override
	public RpcResponse send(RpcRequest request) throws Exception {
		
		byte[] requestBytes = serializer.serialize(request);
		byte[] responseBytes = HttpClientUtil.postRequest(serverAddress, requestBytes);
		return (RpcResponse) serializer.deserialize(responseBytes, RpcResponse.class);
		
	}

}
