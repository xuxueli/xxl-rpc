package com.xxl.rpc.netcom.mina.client;

import com.xxl.rpc.registry.ZkServiceDiscovery;
import com.xxl.rpc.serialize.Serializer;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.concurrent.ConcurrentHashMap;

/**
 * connect pool
 * @author xuxueli 2015-11-5 22:05:38
 */
public class MinaClientPool {
	
	private GenericObjectPool<MinaClientPoolProxy> pool;
	public MinaClientPool(String host, int port, Serializer serializer) {
		pool = new GenericObjectPool<MinaClientPoolProxy>(new MinaClientPoolFactory(host, port, serializer));
		//pool.setTestOnBorrow(true);
		//pool.setMaxTotal(20);
	}
	
	public GenericObjectPool<MinaClientPoolProxy> getPool(){
		return this.pool;
	}

	// serverAddress : [NettyClientPoolProxy01, NettyClientPoolProxy02]
	private static ConcurrentHashMap<String, MinaClientPool> clientPoolMap = new ConcurrentHashMap<String, MinaClientPool>();
	public static GenericObjectPool<MinaClientPoolProxy> getPool(String serverAddress, String className, Serializer serializer)
			throws Exception {

		// valid serverAddress
		if (serverAddress==null || serverAddress.trim().length()==0) {
			serverAddress = ZkServiceDiscovery.discover(className);
		}
		if (serverAddress == null || serverAddress.trim().length() == 0) {
			throw new IllegalArgumentException(">>>>>>>>>>>> serverAddress is null");
		}

		// get from pool
		MinaClientPool clientPool = clientPoolMap.get(serverAddress);
		if (clientPool != null) {
			return clientPool.getPool();
		}

		// init pool
		String[] array = serverAddress.split(":");
		String host = array[0];
		int port = Integer.parseInt(array[1]);

		clientPool = new MinaClientPool(host, port, serializer);
		clientPoolMap.put(serverAddress, clientPool);
		return clientPool.getPool();
	}
	
}
