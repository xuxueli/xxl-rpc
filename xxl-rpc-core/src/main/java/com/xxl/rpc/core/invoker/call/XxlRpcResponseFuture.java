package com.xxl.rpc.core.invoker.call;

import com.xxl.rpc.core.invoker.InvokerFactory;
import com.xxl.rpc.core.register.entity.RegisterInstance;
import com.xxl.rpc.core.remoting.entity.XxlRpcRequest;
import com.xxl.rpc.core.remoting.entity.XxlRpcResponse;
import com.xxl.rpc.core.util.XxlRpcException;
import org.slf4j.Logger;

import java.util.concurrent.*;

/**
 * call back future
 *
 * @author xuxueli 2015-11-5 14:26:37
 */
public class XxlRpcResponseFuture implements Future<XxlRpcResponse> {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(XxlRpcResponseFuture.class);

	// ---------------------- invoke data ----------------------

	// factory
	private final InvokerFactory invokerFactory;
	private final RegisterInstance registerInstance;

	// net data
	private final XxlRpcRequest request;
	private XxlRpcResponse response;

	// future lock
	private volatile boolean done = false;
	private Object lock = new Object();

	// callback, can be null
	private XxlRpcInvokeCallback invokeCallback;


	public XxlRpcResponseFuture(final InvokerFactory invokerFactory,
								final RegisterInstance registerInstance,
								final XxlRpcRequest request,
								XxlRpcInvokeCallback invokeCallback) {

		this.invokerFactory = invokerFactory;
		this.registerInstance = registerInstance;
		this.request = request;
		this.invokeCallback = invokeCallback;

		// set-InvokerFuture
		setInvokerFuture();
	}

	// get
	public XxlRpcRequest getRequest() {
		return request;
	}
	public XxlRpcInvokeCallback getInvokeCallback() {
		return invokeCallback;
	}


	// ---------------------- invoke-future store-opt ----------------------

	/**
	 * set-InvokerFuture
	 */
	public void setInvokerFuture(){
		this.invokerFactory.setInvokerFuture(request.getRequestId(), this);
	}

	/**
	 * remove-InvokerFuture
	 */
	public void removeInvokerFuture(){
		this.invokerFactory.removeInvokerFuture(request.getRequestId());
	}


	// ---------------------- write response ----------------------

	public void setResponse(XxlRpcResponse response) {
		this.response = response;
		synchronized (lock) {
			done = true;
			lock.notifyAll();
		}
	}


	// ---------------------- get response ----------------------

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		// pass, not support
		return false;
	}

	@Override
	public boolean isCancelled() {
		// pass, not support
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
			throw new XxlRpcException(e);
		}
	}

	@Override
	public XxlRpcResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		// get
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
					logger.error(e.getMessage(), e);
				}
			}
		}
		if (!done) {
			throw new XxlRpcException("xxl-rpc, request timeout. registerInstance="+registerInstance.getUniqueKey()+", request=" + request.toString());
		}
		return response;
	}

}
