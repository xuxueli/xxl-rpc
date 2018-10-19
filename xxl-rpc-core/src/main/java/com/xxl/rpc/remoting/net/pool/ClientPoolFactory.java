package com.xxl.rpc.remoting.net.pool;

import com.xxl.rpc.serialize.Serializer;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * pool factory
 * @author xuxueli 2015-11-5 22:07:35
 */
public class ClientPoolFactory extends BasePooledObjectFactory<ClientPooled> {
	
	private String host;
	private int port;
	private Serializer serializer;

	private Class<? extends ClientPooled> clientPoolImpl;

	public ClientPoolFactory(String host, int port, Serializer serializer, Class<? extends ClientPooled> clientPoolImpl) {
		this.host = host;
		this.port = port;
		this.serializer = serializer;
		this.clientPoolImpl = clientPoolImpl;
	}

	@Override
	public ClientPooled create() throws Exception {

		ClientPooled clientPooled = clientPoolImpl.newInstance();
		clientPooled.init(host, port, serializer);

		return clientPooled;
	}

	@Override
	public PooledObject<ClientPooled> wrap(ClientPooled arg0) {
		return new DefaultPooledObject<ClientPooled>(arg0);
	}

	@Override
	public void destroyObject(PooledObject<ClientPooled> p)
			throws Exception {
		ClientPooled clientPooled = p.getObject();
		clientPooled.close();
	}

	@Override
	public boolean validateObject(PooledObject<ClientPooled> p) {
		ClientPooled clientPooled = p.getObject();
		return clientPooled.isValidate();
	}
	

}
