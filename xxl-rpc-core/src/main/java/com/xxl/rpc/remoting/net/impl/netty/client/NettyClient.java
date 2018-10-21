package com.xxl.rpc.remoting.net.impl.netty.client;

import com.xxl.rpc.remoting.net.Client;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.pool.ClientPooled;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * netty client
 * @author xuxueli 2015-11-24 22:25:15
 */
public class NettyClient extends Client {

	@Override
	public void asyncSend(String address, XxlRpcRequest xxlRpcRequest) throws Exception {

		// client pool	[tips03 : may save 35ms/100invoke if move it to constructor, but it is necessary. cause by ConcurrentHashMap.get]
		GenericObjectPool<ClientPooled> clientPool = ClientPooled.getPool(address, xxlRpcReferenceBean.getSerializer(), NettyPooledClient.class);
        // client proxt
		ClientPooled clientPoolProxy = null;

		try {
			// proxy borrow
			clientPoolProxy = clientPool.borrowObject();

			// do invoke
			clientPoolProxy.send(xxlRpcRequest);
		} catch (Exception e) {
			throw e;
		} finally{
			// proxy return
			if (clientPoolProxy != null) {
				clientPool.returnObject(clientPoolProxy);
			}
		}
		
	}

}
