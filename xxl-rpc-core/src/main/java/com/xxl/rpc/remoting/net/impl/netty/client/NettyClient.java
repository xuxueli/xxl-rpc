package com.xxl.rpc.remoting.net.impl.netty.client;

import com.xxl.rpc.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.remoting.net.Client;
import com.xxl.rpc.remoting.net.params.RpcCallbackFuture;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import com.xxl.rpc.remoting.net.pool.ClientPooled;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * netty client
 * @author xuxueli 2015-11-24 22:25:15
 */
public class NettyClient extends Client {

	@Override
	public XxlRpcResponse send(String address, XxlRpcRequest xxlRpcRequest) throws Exception {

		// client pool	[tips03 : may save 35ms/100invoke if move it to constructor, but it is necessary. cause by ConcurrentHashMap.get]
		GenericObjectPool<ClientPooled> clientPool = XxlRpcInvokerFactory.getPool(address, xxlRpcReferenceBean.getSerializer(), NettyPooledClient.class);
        // client proxt
		ClientPooled clientPoolProxy = null;

		try {
			// future set	[tips04 : may save 20ms/100invoke if remove and wait for channel instead, but it is necessary. cause by ConcurrentHashMap.get]
			RpcCallbackFuture future = new RpcCallbackFuture(xxlRpcRequest);
			XxlRpcInvokerFactory.setInvokerFuture(xxlRpcRequest.getRequestId(), future);

			// proxy borrow
			clientPoolProxy = clientPool.borrowObject();

			// do invoke
			clientPoolProxy.send(xxlRpcRequest);

			// future get
			return future.get(xxlRpcReferenceBean.getTimeout());
		} catch (Exception e) {
			throw e;
		} finally{

			// future remove
			XxlRpcInvokerFactory.removeInvokerFuture(xxlRpcRequest.getRequestId());

			// proxy return
			if (clientPoolProxy != null) {
				clientPool.returnObject(clientPoolProxy);
			}
		}
		
	}

}
