package com.xxl.rpc.remoting.net.impl.mina.client;

import com.xxl.rpc.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.remoting.net.Client;
import com.xxl.rpc.remoting.net.params.RpcCallbackFuture;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import com.xxl.rpc.remoting.net.pool.ClientPooled;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * mina client
 *
 * @author xuxueli 2015-11-24 22:25:15
 */
public class MinaClient extends Client {

	@Override
	public XxlRpcResponse send(String address, XxlRpcRequest xxlRpcRequest) throws Exception {

		// client pool
    	GenericObjectPool<ClientPooled> clientPool = XxlRpcInvokerFactory.getPool(address, xxlRpcReferenceBean.getSerializer(), MinaPooledClient.class);
    	// client proxy
		ClientPooled clientPoolProxy = null;

		try {
			// future set
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
