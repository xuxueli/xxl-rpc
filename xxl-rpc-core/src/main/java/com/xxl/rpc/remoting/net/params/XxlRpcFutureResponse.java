package com.xxl.rpc.remoting.net.params;

import com.xxl.rpc.util.XxlRpcException;

import java.text.MessageFormat;
import java.util.concurrent.TimeoutException;

/**
 * call back future, make netty-rpc synchronous on asynchronous model
 * @author xuxueli 2015-11-5 14:26:37
 * 
 * v1: Synchroniz + notifyAll + ctx.writeAndFlush(xxlRpcResponse).addListener(ChannelFutureListener.CLOSE);
 * v2: Map isDone + ctx.writeAndFlush(xxlRpcResponse)
 * v3: Map synchronized wait + notifyAll
 */
public class XxlRpcFutureResponse {

	// net codec
	private XxlRpcRequest xxlRpcRequest;
	private XxlRpcResponse xxlRpcResponse;

	// future lock
	private boolean isDone = false;
	private Object lock = new Object();


	public XxlRpcFutureResponse(XxlRpcRequest xxlRpcRequest) {
		this.xxlRpcRequest = xxlRpcRequest;
	}


	public XxlRpcRequest getXxlRpcRequest() {
		return xxlRpcRequest;
	}
	public XxlRpcResponse getXxlRpcResponse() {
		return xxlRpcResponse;
	}

	public void setXxlRpcResponse(XxlRpcResponse xxlRpcResponse) {
		this.xxlRpcResponse = xxlRpcResponse;
		// notify future lock
		synchronized (lock) {
			isDone = true;
			lock.notifyAll();
		}
	}

	public XxlRpcResponse get(long timeoutMillis) throws InterruptedException, TimeoutException{
		if (!isDone) {
			synchronized (lock) {
				try {
					lock.wait(timeoutMillis);
				} catch (InterruptedException e) {
					throw e;
				}
			}
		}
		
		if (!isDone) {
			throw new XxlRpcException("xxl-rpc, request timeout at:"+ System.currentTimeMillis() +", XxlRpcRequest:" + xxlRpcRequest.toString());
		}
		return xxlRpcResponse;
	}

}
