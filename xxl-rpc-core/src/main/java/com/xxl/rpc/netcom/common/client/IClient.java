package com.xxl.rpc.netcom.common.client;

import com.xxl.rpc.netcom.common.NetComEnum;
import com.xxl.rpc.netcom.common.codec.RpcRequest;
import com.xxl.rpc.netcom.common.codec.RpcResponse;
import com.xxl.rpc.serialize.Serializer;

/**
 * i client
 * @author xuxueli 2015-11-24 22:18:10
 */
public abstract class IClient {
	
	// init config
	protected String serverAddress;
	protected Serializer serializer;
	protected long timeoutMillis;
	
	private void initConfig(String serverAddress, String serialize, long timeoutMillis) {
		this.serverAddress = serverAddress;
		this.serializer = Serializer.SerializeEnum.match(serialize, Serializer.SerializeEnum.HESSIAN).serializer;
		this.timeoutMillis = timeoutMillis;
	}
	
	public abstract RpcResponse send(RpcRequest request) throws Exception;
	
	public static IClient getInstance(String netcom_type, String serverAddress, String serialize, long timeoutMillis){
		NetComEnum netCom = NetComEnum.match(netcom_type, NetComEnum.NETTY);
		
		IClient client = null;
		try {
			client = netCom.clientClass.newInstance();
			client.initConfig(serverAddress, serialize, timeoutMillis);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return client;
	}
	
}
