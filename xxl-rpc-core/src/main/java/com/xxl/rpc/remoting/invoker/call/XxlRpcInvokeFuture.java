package com.xxl.rpc.remoting.invoker.call;

import java.util.concurrent.Future;

public class XxlRpcInvokeFuture {


    // ---------------------- thread invoke future ----------------------

    private static ThreadLocal<Future<?>> threadInvokerFuture = new ThreadLocal<Future<?>>();


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
    public static void setFuture(Future<?> future) {
        threadInvokerFuture.set(future);
    }

    /**
     * remove future
     */
    public static void removeFuture() {
        threadInvokerFuture.remove();
    }

}

