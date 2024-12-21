package com.xxl.rpc.core.remoting;

import com.xxl.rpc.core.factory.XxlRpcFactory;
import com.xxl.rpc.core.invoker.reference.XxlRpcReferenceBean;
import com.xxl.rpc.core.remoting.params.XxlRpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * i client
 * @author xuxueli 2015-11-24 22:18:10
 */
public abstract class Client {
	protected static final Logger logger = LoggerFactory.getLogger(Client.class);


	// ---------------------- init ----------------------

	protected volatile XxlRpcReferenceBean xxlRpcReferenceBean;

	public void init(XxlRpcReferenceBean xxlRpcReferenceBean) {
		this.xxlRpcReferenceBean = xxlRpcReferenceBean;
	}


    // ---------------------- send ----------------------

	/**
	 * async send, bind requestId and future-response
	 *
	 * @param address
	 * @param xxlRpcRequest
	 * @return
	 * @throws Exception
	 */
	public abstract void asyncSend(String address, XxlRpcRequest xxlRpcRequest, final XxlRpcFactory factory) throws Exception;

}
