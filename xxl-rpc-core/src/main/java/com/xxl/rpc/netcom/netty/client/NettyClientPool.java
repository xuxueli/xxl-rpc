package com.xxl.rpc.netcom.netty.client;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.pool2.impl.GenericObjectPool;

import com.xxl.rpc.registry.ZkServiceDiscovery;
import com.xxl.rpc.serialize.Serializer;

/**
 * connect pool
 * @author xuxueli 2015-11-5 22:05:38
 */
public class NettyClientPool {
	
	private GenericObjectPool<NettyClientPoolProxy> pool;
	public NettyClientPool(String host, int port, Serializer serializer) {
		pool = new GenericObjectPool<NettyClientPoolProxy>(new NettyClientPoolFactory(host, port, serializer));
		//pool.setTestOnBorrow(true);
		//pool.setMaxTotal(20);
	}
	
	public GenericObjectPool<NettyClientPoolProxy> getPool(){
		return this.pool;
	}

	// serverAddress : [NettyClientPoolProxy01, NettyClientPoolProxy02]
	private static ConcurrentHashMap<String, NettyClientPool> clientPoolMap = new ConcurrentHashMap<String, NettyClientPool>();
	public static GenericObjectPool<NettyClientPoolProxy> getPool(boolean zookeeper_switch, String serverAddress, String className, Serializer serializer)
			throws Exception {
		if (zookeeper_switch) {
			serverAddress = ZkServiceDiscovery.zkServiceDiscovery.discover(className);
		}
		NettyClientPool clientPool = clientPoolMap.get(serverAddress);

		if (clientPool != null) {
			return clientPool.getPool();
		}

		if (serverAddress == null || serverAddress.trim().length() == 0) {
			throw new IllegalArgumentException(">>>>>>>>>>>> serverAddress is null");
		}

		String[] array = serverAddress.split(":");
		String host = array[0];
		int port = Integer.parseInt(array[1]);

		clientPool = new NettyClientPool(host, port, serializer);
		clientPoolMap.put(serverAddress, clientPool);
		return clientPool.getPool();
	}
	
}
