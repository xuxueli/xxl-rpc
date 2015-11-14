package com.xxl.rpc.netcom.common.codec;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

/**
 * call back future, make netty-rpc synchronous on asynchronous model
 * @author xuxueli 2015-11-5 14:26:37
 * 
 * v1: Synchroniz + notifyAll + ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
 * v2: Map isDone + ctx.writeAndFlush(response)
 * v3: Map synchronized wait + notifyAll
 */
public class RpcCallbackFuture {
	public static ConcurrentMap<String, RpcCallbackFuture> futurePool = new ConcurrentHashMap<String, RpcCallbackFuture>();	// 过期，失效
	
	// net codec
	private RpcRequest request;
	private RpcResponse response;
	// future lock
	private boolean isDone = false;
	private Object lock = new Object();
	
	public RpcCallbackFuture(RpcRequest request) {
		this.request = request;
		futurePool.put(request.getRequestId(), this);
	}
	public RpcResponse getResponse() {
		return response;
	}
	public void setResponse(RpcResponse response) {
		this.response = response;
		// notify future lock
		synchronized (lock) {
			isDone = true;
			lock.notifyAll();
		}
	}

	public RpcResponse get(long timeoutMillis) throws InterruptedException, TimeoutException{
		if (!isDone) {
			synchronized (lock) {
				try {
					lock.wait(timeoutMillis);
				} catch (InterruptedException e) {
					e.printStackTrace();
					throw e;
				}
			}
		}
		
		if (!isDone) {
			throw new TimeoutException(MessageFormat.format(">>>>>>>>>>>> xxl-rpc, netty request timeout at:{0}, request:{1}", System.currentTimeMillis(), request.toString()));
		}
		return response;
	}
}
