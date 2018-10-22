package com.xxl.rpc.remoting.net.params;

import com.xxl.rpc.util.XxlRpcException;

import java.util.concurrent.*;

/**
 * call back future, make netty-rpc synchronous on asynchronous model
 * @author xuxueli 2015-11-5 14:26:37
 * 
 * v1: Synchroniz + notifyAll + ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
 * v2: Map done + ctx.writeAndFlush(response)
 * v3: Map synchronized wait + notifyAll
 */
public class XxlRpcFutureResponse implements Future<XxlRpcResponse> {


	// net data
	private XxlRpcRequest request;
	private XxlRpcResponse response;

	// future lock
	private boolean done = false;
	private Object lock = new Object();


	public XxlRpcFutureResponse(XxlRpcRequest request) {
		this.request = request;
	}


	public void setResponse(XxlRpcResponse response) {
		this.response = response;
		// notify future lock
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


	// ---------------------- future pool (static) ----------------------

	private static ConcurrentMap<String, XxlRpcFutureResponse> futureResponsePool = new ConcurrentHashMap<String, XxlRpcFutureResponse>();

	public static void setInvokerFuture(String requestId, XxlRpcFutureResponse futureResponse){
		futureResponsePool.put(requestId, futureResponse);
	}
	public static void removeInvokerFuture(String requestId){
		futureResponsePool.remove(requestId);
	}
	public static XxlRpcFutureResponse getInvokerFuture(String requestId){
		return futureResponsePool.get(requestId);
	}


}
