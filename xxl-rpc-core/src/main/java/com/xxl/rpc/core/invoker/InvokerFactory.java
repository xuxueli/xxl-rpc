package com.xxl.rpc.core.invoker;

import com.xxl.rpc.core.factory.XxlRpcFactory;
import com.xxl.rpc.core.remoting.params.XxlRpcFuture;
import com.xxl.rpc.core.remoting.params.XxlRpcResponse;
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


    /**
     * factory link
     */
    private XxlRpcFactory factory;

    /**
     * start
     *
     * @param factory
     * @throws Exception
     */
    public void start(final XxlRpcFactory factory) throws Exception {

        // link
        this.factory = factory;

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



    // ---------------------- async-future, response-store ----------------------

    /**
     * future Response Pool
     */
    private ConcurrentMap<String, XxlRpcFuture> futureResponseStore = new ConcurrentHashMap<String, XxlRpcFuture>();

    /**
     * set
     *
     * @param requestId
     * @param futureResponse
     */
    public void setInvokerFuture(String requestId, XxlRpcFuture futureResponse){
        futureResponseStore.put(requestId, futureResponse);
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

        // get
        final XxlRpcFuture rpcFuture = futureResponseStore.get(requestId);
        if (rpcFuture == null) {
            return;
        }
        // do remove
        futureResponseStore.remove(requestId);

        // set response, parse result
        rpcFuture.setResponse(xxlRpcResponse);

        // notify
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


    // ---------------------- async callback, run ThreadPool ----------------------

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

        // lazy init
        if (responseCallbackThreadPool == null) {
            synchronized (this) {
                if (responseCallbackThreadPool == null) {
                    responseCallbackThreadPool = new ThreadPoolExecutor(
                            5,
                            100,
                            60L,
                            TimeUnit.SECONDS,
                            new LinkedBlockingQueue<Runnable>(1000),
                            new ThreadFactory() {
                                @Override
                                public Thread newThread(Runnable r) {
                                    return new Thread(r, "xxl-rpc, XxlRpcInvokerFactory-responseCallbackThreadPool-" + r.hashCode());
                                }
                            },
                            new RejectedExecutionHandler() {
                                @Override
                                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                                    throw new XxlRpcException("xxl-rpc, XxlRpcInvokerFactory-responseCallbackThreadPool is EXHAUSTED!");
                                }
                            });		// default maxThreads 300, minThreads 60
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
