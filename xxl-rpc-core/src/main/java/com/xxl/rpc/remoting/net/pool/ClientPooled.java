package com.xxl.rpc.remoting.net.pool;

import com.xxl.rpc.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.remoting.net.params.BaseCallback;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.serialize.Serializer;
import com.xxl.rpc.util.IpUtil;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xuxueli 2018-10-19
 */
public abstract class ClientPooled {
    protected static transient Logger logger = LoggerFactory.getLogger(ClientPooled.class);


    // ---------------------- iface ----------------------

    public abstract void init(String host, int port, final Serializer serializer, final XxlRpcInvokerFactory xxlRpcInvokerFactory) throws Exception;

    public abstract void close();

    public abstract boolean isValidate();

    public abstract void send(XxlRpcRequest xxlRpcRequest) throws Exception ;


    // ---------------------- client pool map ----------------------

    private static ConcurrentHashMap<String, GenericObjectPool<ClientPooled>> clientPoolMap;        // (static) alread addStopCallBack
    public static GenericObjectPool<ClientPooled> getPool(String address, Class<? extends ClientPooled> clientPoolImpl, Serializer serializer, final XxlRpcInvokerFactory xxlRpcInvokerFactory) throws Exception {

        // init client-pool-map, avoid repeat init
        if (clientPoolMap == null) {
            synchronized (ClientPooled.class) {
                if (clientPoolMap == null) {
                    // init
                    clientPoolMap = new ConcurrentHashMap<String, GenericObjectPool<ClientPooled>>();
                    // stop callback
                    xxlRpcInvokerFactory.addStopCallBack(new BaseCallback() {
                        @Override
                        public void run() throws Exception {
                            if (clientPoolMap.size() > 0) {
                                for (String key:clientPoolMap.keySet()) {
                                    GenericObjectPool<ClientPooled> clientPool = clientPoolMap.get(key);
                                    clientPool.close();
                                }
                                clientPoolMap.clear();
                            }
                        }
                    });
                }
            }
        }

        // get pool
        GenericObjectPool<ClientPooled> clientPool = clientPoolMap.get(address);
        if (clientPool != null) {
            return clientPool;
        }

        // make pool, avoid repeat init
        synchronized (clientPoolMap) {

            // re-get pool
            clientPool = clientPoolMap.get(address);
            if (clientPool != null) {
                return clientPool;
            }

            // parse address
            Object[] array = IpUtil.parseIpPort(address);
            String host = (String) array[0];
            int port = (int) array[1];

            // set pool
            clientPool = new GenericObjectPool<ClientPooled>(new ClientPoolFactory(clientPoolImpl, host, port, serializer, xxlRpcInvokerFactory));
            clientPool.setTestOnBorrow(true);
            clientPool.setMaxTotal(2);

            clientPoolMap.put(address, clientPool);

            return clientPool;
        }

    }

}
