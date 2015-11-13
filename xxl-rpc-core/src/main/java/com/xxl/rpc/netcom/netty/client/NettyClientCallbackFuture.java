package com.xxl.rpc.netcom.netty.client;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

import com.xxl.rpc.netcom.netty.codec.NettyRequest;
import com.xxl.rpc.netcom.netty.codec.NettyResponse;

/**
 * call back future, make netty-rpc synchronous on asynchronous model
 * @author xuxueli 2015-11-5 14:26:37
 * 
 * v1: Synchroniz + notifyAll + ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
 * v2: Map isDone + ctx.writeAndFlush(response)
 * v3: Map synchronized wait + notifyAll
 */
public class NettyClientCallbackFuture {
	public static ConcurrentMap<String, NettyClientCallbackFuture> futurePool = new ConcurrentHashMap<String, NettyClientCallbackFuture>();	// 过期，失效
	
	// net codec
	private NettyRequest request;
	private NettyResponse response;
	// future lock
	private boolean isDone = false;
	private Object lock = new Object();
	
	public NettyClientCallbackFuture(NettyRequest request) {
		this.request = request;
		futurePool.put(request.getRequestId(), this);
	}
	public NettyResponse getResponse() {
		return response;
	}
	public void setResponse(NettyResponse response) {
		this.response = response;
		// notify future lock
		synchronized (lock) {
			isDone = true;
			lock.notifyAll();
		}
	}

	public NettyResponse get(long timeoutMillis) throws InterruptedException, TimeoutException{
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
			throw new TimeoutException(MessageFormat.format(">>>>>>>>>>>> xxl-rpc, netty request timeout at:{}, request:{}", System.currentTimeMillis(), request.toString()));
		}
		return response;
	}
	
}
