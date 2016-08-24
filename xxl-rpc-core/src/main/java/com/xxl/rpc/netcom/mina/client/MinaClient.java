package com.xxl.rpc.netcom.mina.client;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.rpc.netcom.common.client.IClient;
import com.xxl.rpc.netcom.common.codec.RpcCallbackFuture;
import com.xxl.rpc.netcom.common.codec.RpcRequest;
import com.xxl.rpc.netcom.common.codec.RpcResponse;

/**
 * mina client
 * @author xuxueli 2015-11-24 22:25:15
 */
public class MinaClient extends IClient {
	private static Logger logger = LoggerFactory.getLogger(MinaClient.class);

	@Override
	public RpcResponse send(RpcRequest request) throws Exception {
		// client pool
    	GenericObjectPool<MinaClientPoolProxy> clientPool = MinaClientPool.getPool(serverAddress, request.getClassName(), serializer);
    	// client proxy
    	MinaClientPoolProxy clientPoolProxy = null;
		try {
			// future init
			RpcCallbackFuture future = new RpcCallbackFuture(request);
			RpcCallbackFuture.futurePool.put(request.getRequestId(), future);
			
			// rpc invoke
			clientPoolProxy = clientPool.borrowObject();
			clientPoolProxy.send(request);
			
			// future get
			return future.get(timeoutMillis);
		} catch (Exception e) {
			logger.error("", e);
			throw e;
		} finally{
			RpcCallbackFuture.futurePool.remove(request.getRequestId());
			clientPool.returnObject(clientPoolProxy);
		}
		
	}

}
