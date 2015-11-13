package com.xxl.rpc.netcom.netty.client;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.xxl.rpc.serialize.Serializer;

/**
 * pool factory
 * @author xuxueli 2015-11-5 22:07:35
 */
public class NettyClientPoolFactory extends BasePooledObjectFactory<NettyClientPoolProxy> {
	
	private String host;
	private int port;
	private Serializer serializer;
	public NettyClientPoolFactory(String host, int port, Serializer serializer) {
		this.host = host;
		this.port = port;
		this.serializer = serializer;
	}

	@Override
	public NettyClientPoolProxy create() throws Exception {
		NettyClientPoolProxy NettyClientProxy = new NettyClientPoolProxy();
		NettyClientProxy.createProxy(host, port, serializer);
		return NettyClientProxy;
	}

	@Override
	public PooledObject<NettyClientPoolProxy> wrap(NettyClientPoolProxy arg0) {
		return new DefaultPooledObject<NettyClientPoolProxy>(arg0);
	}

	@Override
	public void destroyObject(PooledObject<NettyClientPoolProxy> p)
			throws Exception {
		NettyClientPoolProxy NettyClientProxy = p.getObject();
		NettyClientProxy.close();
	}

	@Override
	public boolean validateObject(PooledObject<NettyClientPoolProxy> p) {
		NettyClientPoolProxy NettyClientProxy = p.getObject();
		return NettyClientProxy.isValidate();
	}
	

}
