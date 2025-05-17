package com.xxl.rpc.core.invoker.call;

import com.xxl.rpc.core.remoting.entity.XxlRpcResponse;
import com.xxl.rpc.core.util.XxlRpcException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class XxlRpcInvokeFuture implements Future {

    private XxlRpcResponseFuture responseFuture;

    public XxlRpcInvokeFuture(XxlRpcResponseFuture responseFuture) {
        this.responseFuture = responseFuture;
    }
    public void remove(){
        // remove-InvokerFuture
        responseFuture.removeInvokerFuture();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return responseFuture.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return responseFuture.isCancelled();
    }

    @Override
    public boolean isDone() {
        return responseFuture.isDone();
    }

    @Override
    public Object get() throws ExecutionException, InterruptedException {
        try {
            return get(-1, TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            throw new XxlRpcException(e);
        }
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        try {
            // future get
            XxlRpcResponse xxlRpcResponse = responseFuture.get(timeout, unit);
            if (xxlRpcResponse.getErrorMsg() != null) {
                throw new XxlRpcException(xxlRpcResponse.getErrorMsg());
            }
            return xxlRpcResponse.getResult();
        } finally {
            remove();
        }
    }


    // ---------------------- thread invoke future ----------------------

    private static ThreadLocal<XxlRpcInvokeFuture> threadInvokerFuture = new ThreadLocal<>();

    /**
     * get future
     *
     * @param type
     * @param <T>
     * @return
     */
    public static <T> Future<T> getFuture(Class<T> type) {
        Future<T> future = (Future<T>) threadInvokerFuture.get();
        threadInvokerFuture.remove();
        return future;
    }

    /**
     * set future
     *
     * @param future
     */
    public static void setFuture(XxlRpcInvokeFuture future) {
        threadInvokerFuture.set(future);
    }

    /**
     * remove future
     */
    public static void removeFuture() {
        threadInvokerFuture.remove();
    }

}
