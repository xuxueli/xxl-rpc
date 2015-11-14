package com.xxl.rpc.netcom.mina.client;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.xxl.rpc.serialize.Serializer;

/**
 * pool factory
 * @author xuxueli 2015-11-5 22:07:35
 */
public class MinaClientPoolFactory extends BasePooledObjectFactory<MinaClientPoolProxy> {
	
	private String host;
	private int port;
	private Serializer serializer;
	public MinaClientPoolFactory(String host, int port, Serializer serializer) {
		this.host = host;
		this.port = port;
		this.serializer = serializer;
	}

	@Override
	public MinaClientPoolProxy create() throws Exception {
		MinaClientPoolProxy NettyClientProxy = new MinaClientPoolProxy();
		NettyClientProxy.createProxy(host, port, serializer);
		return NettyClientProxy;
	}

	@Override
	public PooledObject<MinaClientPoolProxy> wrap(MinaClientPoolProxy arg0) {
		return new DefaultPooledObject<MinaClientPoolProxy>(arg0);
	}

	@Override
	public void destroyObject(PooledObject<MinaClientPoolProxy> p)
			throws Exception {
		MinaClientPoolProxy NettyClientProxy = p.getObject();
		NettyClientProxy.close();
	}

	@Override
	public boolean validateObject(PooledObject<MinaClientPoolProxy> p) {
		MinaClientPoolProxy NettyClientProxy = p.getObject();
		return NettyClientProxy.isValidate();
	}
	

}
