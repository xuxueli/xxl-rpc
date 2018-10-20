package com.xxl.rpc.remoting.net;

import com.xxl.rpc.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.remoting.invoker.reference.XxlRpcReferenceBean;
import com.xxl.rpc.remoting.net.params.XxlRpcFutureResponse;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * i client
 * @author xuxueli 2015-11-24 22:18:10
 */
public abstract class Client {
	protected static final Logger logger = LoggerFactory.getLogger(Client.class);


	// ---------------------- init ----------------------

	protected XxlRpcReferenceBean xxlRpcReferenceBean;

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
	public abstract void asyncSend(String address, XxlRpcRequest xxlRpcRequest) throws Exception;

	/**
	 * sync send
	 *
	 * @param address
	 * @param xxlRpcRequest
	 * @return
	 * @throws Exception
	 */
	public XxlRpcResponse syncSend(String address, XxlRpcRequest xxlRpcRequest) throws Exception {
		try {
			// future set
			XxlRpcFutureResponse futureResponse = new XxlRpcFutureResponse(xxlRpcRequest);
			XxlRpcInvokerFactory.setInvokerFuture(xxlRpcRequest.getRequestId(), futureResponse);

			// do invoke
			asyncSend(address, xxlRpcRequest);

			// future get
			return futureResponse.get(xxlRpcReferenceBean.getTimeout());
		} catch (Exception e) {
			throw e;
		} finally{
			// future remove
			XxlRpcInvokerFactory.removeInvokerFuture(xxlRpcRequest.getRequestId());
		}
	}



}
