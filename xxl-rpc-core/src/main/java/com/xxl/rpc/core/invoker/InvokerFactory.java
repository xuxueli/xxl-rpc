package com.xxl.rpc.core.invoker;

import com.xxl.rpc.core.boot.XxlRpcBootstrap;
import com.xxl.rpc.core.invoker.call.XxlRpcResponseFuture;
import com.xxl.rpc.core.register.entity.RegisterInstance;
import com.xxl.rpc.core.remoting.Client;
import com.xxl.rpc.core.remoting.entity.XxlRpcResponse;
import com.xxl.rpc.core.serializer.Serializer;
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
    private final XxlRpcBootstrap factory;

    public InvokerFactory(final XxlRpcBootstrap xxlRpcFactory) {
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

        // destory connect client
        destoryClient();
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
            try {
                responseCallbackThreadPool.shutdown();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    // ---------------------- client-pool ----------------------

    private volatile ConcurrentMap<String, Client> connectClientMap = new ConcurrentHashMap<>();
    private volatile ConcurrentMap<String, Object> connectClientLockMap = new ConcurrentHashMap<>();

    /**
     * destory connect client
     */
    private void destoryClient(){
        if (!connectClientMap.isEmpty()) {
            for (String key: connectClientMap.keySet()) {
                Client clientPool = connectClientMap.get(key);
                try {
                    clientPool.close();
                } catch (Throwable e) {
                    logger.error(e.getMessage(), e);
                }
            }
            connectClientMap.clear();
        }
    }

    /**
     * remove client
     *
     * @param address
     */
    public void checkDeadAndRemoveClient(String address){
        Client connectClient = connectClientMap.get(address);
        if (connectClient!=null) {
            if (!connectClient.isValidate()) {
                connectClientMap.remove(address);
            }
        }
    }

    /**
     * get client with pool
     *
     * @param registerInstance
     * @param clientClass
     * @param serializer
     * @return
     * @throws Exception
     */
    public Client getClient(RegisterInstance registerInstance, Class<? extends Client> clientClass, final Serializer serializer) throws Exception {

        String uniqueKey = registerInstance.getUniqueKey();

        // get-valid client
        Client connectClient = connectClientMap.get(uniqueKey);
        if (connectClient!=null && connectClient.isValidate()) {
            return connectClient;
        }

        // lock
        Object clientLock = connectClientLockMap.get(uniqueKey);
        if (clientLock == null) {
            connectClientLockMap.putIfAbsent(uniqueKey, new Object());
            clientLock = connectClientLockMap.get(uniqueKey);
        }

        // remove-create new client
        synchronized (clientLock) {

            // get-valid client, avlid repeat
            connectClient = connectClientMap.get(uniqueKey);
            if (connectClient!=null && connectClient.isValidate()) {
                return connectClient;
            }

            // remove old
            if (connectClient != null) {
                connectClient.close();
                connectClientMap.remove(uniqueKey);
            }

            // set pool
            Client connectClient_new = clientClass.newInstance();
            try {
                connectClient_new.init(registerInstance, serializer, factory);
                connectClientMap.put(uniqueKey, connectClient_new);
            } catch (Exception e) {
                connectClient_new.close();
                throw e;
            }

            return connectClient_new;
        }
    }

}
