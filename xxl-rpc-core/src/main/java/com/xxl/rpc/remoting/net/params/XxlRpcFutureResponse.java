package com.xxl.rpc.remoting.net.params;

import com.xxl.rpc.remoting.invoker.call.XxlRpcInvokeCallback;
import com.xxl.rpc.util.XxlRpcException;

import java.util.concurrent.*;

/**
 * call back future
 *
 * @author xuxueli 2015-11-5 14:26:37
 */
public class XxlRpcFutureResponse implements Future<XxlRpcResponse> {


	// net data
	private XxlRpcRequest request;
	private XxlRpcResponse response;

	// future lock
	private boolean done = false;
	private Object lock = new Object();

	// callback
	private XxlRpcInvokeCallback invokeCallback;


	public XxlRpcFutureResponse(XxlRpcRequest request, XxlRpcInvokeCallback invokeCallback) {
		this.request = request;
		this.invokeCallback = invokeCallback;
	}

	public XxlRpcRequest getRequest() {
		return request;
	}

	public void setResponse(XxlRpcResponse response) {
		this.response = response;

		// callback: do callback invoke
		if (invokeCallback != null) {
			if (this.response.getErrorMsg() != null) {
				invokeCallback.onFailure(new XxlRpcException(this.response.getErrorMsg()));
			} else {
				invokeCallback.onSuccess(this.response.getResult());
			}
			return;
		}

		// future：notify future lock
		synchronized (lock) {
			done = true;
			lock.notifyAll();
		}
	}


	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		// TODO
		return false;
	}

	@Override
	public boolean isCancelled() {
		// TODO
		return false;
	}

	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public XxlRpcResponse get() throws InterruptedException, ExecutionException {
		try {
			return get(-1, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public XxlRpcResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		if (!done) {
			synchronized (lock) {
				try {
					if (timeout < 0) {
						lock.wait();
					} else {
						long timeoutMillis = (TimeUnit.MILLISECONDS==unit)?timeout:TimeUnit.MILLISECONDS.convert(timeout , unit);
						lock.wait(timeoutMillis);
					}
				} catch (InterruptedException e) {
					throw e;
				}
			}
		}

		if (!done) {
			throw new XxlRpcException("xxl-rpc, request timeout at:"+ System.currentTimeMillis() +", request:" + request.toString());
		}
		return response;
	}


}
