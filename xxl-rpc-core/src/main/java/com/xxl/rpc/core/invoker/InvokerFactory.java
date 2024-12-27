package com.xxl.rpc.core.invoker;

import com.xxl.rpc.core.factory.XxlRpcFactory;
import com.xxl.rpc.core.invoker.call.XxlRpcResponseFuture;
import com.xxl.rpc.core.remoting.entity.XxlRpcResponse;
import com.xxl.rpc.core.util.ThreadPoolUtil;
import com.xxl.rpc.core.util.XxlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * xxl-rpc invoker factory, init service-registry
 *
 * @author xuxueli 2018-10-19
 */
public class InvokerFactory {
    private static Logger logger = LoggerFactory.getLogger(InvokerFactory.class);

    // ---------------------- base ----------------------

    /**
     * factory
     */
    private final XxlRpcFactory factory;

    public InvokerFactory(final XxlRpcFactory xxlRpcFactory) {
        this.factory = xxlRpcFactory;
    }


    // ---------------------- start / stop ----------------------

    /**
     * start
     *
     * @throws Exception
     */
    public void start() throws Exception {
        // start
    }

    /**
     * stop
     *
     * @throws Exception
     */
    public void  stop() throws Exception {

        // stop CallbackThreadPool
        stopCallbackThreadPool();
    }


    // ---------------------- future-response store ----------------------

    /**
     * future Response Pool
     */
    private final ConcurrentMap<String, XxlRpcResponseFuture> futureResponseStore = new ConcurrentHashMap<>();

    /**
     * set
     *
     * @param requestId
     * @param rpcFuture
     */
    public void setInvokerFuture(String requestId, XxlRpcResponseFuture rpcFuture){
        futureResponseStore.put(requestId, rpcFuture);
    }

    /**
     * remove
     *
     * @param requestId
     */
    public void removeInvokerFuture(String requestId){
        futureResponseStore.remove(requestId);
    }

    /**
     * notify, write response-data
     *
     * @param requestId
     * @param xxlRpcResponse
     */
    public void notifyInvokerFuture(String requestId, final XxlRpcResponse xxlRpcResponse){

        // match future
        final XxlRpcResponseFuture rpcFuture = futureResponseStore.get(requestId);
        if (rpcFuture == null) {
            return;
        }
        // remove future
        futureResponseStore.remove(requestId);

        // set response
        rpcFuture.setResponse(xxlRpcResponse);

        // submit callback-data
        if (rpcFuture.getInvokeCallback() != null) {

            // callback-type, async run in thread-pool
            try {
                executeResponseCallback(new Runnable() {
                    @Override
                    public void run() {
                        if (xxlRpcResponse.getErrorMsg() != null) {
                            rpcFuture.getInvokeCallback().onFailure(new XxlRpcException(xxlRpcResponse.getErrorMsg()));
                        } else {
                            rpcFuture.getInvokeCallback().onSuccess(xxlRpcResponse.getResult());
                        }
                    }
                });
            }catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

    }


    // ---------------------- callback ThreadPool ----------------------

    /**
     *
     */
    private ThreadPoolExecutor responseCallbackThreadPool = null;

    /**
     * CallbackThreadPool
     *
     * @param runnable
     */
    public void executeResponseCallback(Runnable runnable){

        // lazy-init responseCallbackThreadPool
        if (responseCallbackThreadPool == null) {
            synchronized (this) {
                if (responseCallbackThreadPool == null) {
                    responseCallbackThreadPool = ThreadPoolUtil.makeServerThreadPool(
                            "InvokerFactory-responseCallbackThreadPool",
                            5 ,
                            100);
                }
            }
        }

        // submit callback runnable
        responseCallbackThreadPool.execute(runnable);
    }

    /**
     * stop CallbackThreadPool
     */
    public void stopCallbackThreadPool() {
        if (responseCallbackThreadPool != null) {
            responseCallbackThreadPool.shutdown();
        }
    }

}
