package com.xxl.rpc.remoting.net.pool;

import com.xxl.rpc.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.serialize.Serializer;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * pool factory
 * @author xuxueli 2015-11-5 22:07:35
 */
public class ClientPoolFactory extends BasePooledObjectFactory<ClientPooled> {

	private Class<? extends ClientPooled> clientPoolImpl;

	private String address;
	private Serializer serializer;
	private XxlRpcInvokerFactory xxlRpcInvokerFactory;

	public ClientPoolFactory(Class<? extends ClientPooled> clientPoolImpl, String address, final Serializer serializer, final XxlRpcInvokerFactory xxlRpcInvokerFactory) {
		this.clientPoolImpl = clientPoolImpl;
		this.address = address;
		this.serializer = serializer;
		this.xxlRpcInvokerFactory = xxlRpcInvokerFactory;
	}

	@Override
	public ClientPooled create() throws Exception {

		ClientPooled clientPooled = clientPoolImpl.newInstance();
		clientPooled.init(address, serializer, xxlRpcInvokerFactory);

		return clientPooled;
	}

	@Override
	public PooledObject<ClientPooled> wrap(ClientPooled arg0) {
		return new DefaultPooledObject<ClientPooled>(arg0);
	}

	@Override
	public void destroyObject(PooledObject<ClientPooled> p) throws Exception {
		ClientPooled clientPooled = p.getObject();
		clientPooled.close();
	}

	@Override
	public boolean validateObject(PooledObject<ClientPooled> p) {
		ClientPooled clientPooled = p.getObject();
		return clientPooled.isValidate();
	}
	

}
