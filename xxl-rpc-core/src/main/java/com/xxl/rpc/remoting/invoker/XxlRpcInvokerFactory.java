package com.xxl.rpc.remoting.invoker;

import com.xxl.rpc.registry.ServiceRegistry;
import com.xxl.rpc.remoting.net.pool.ClientPoolFactory;
import com.xxl.rpc.remoting.net.pool.ClientPooled;
import com.xxl.rpc.serialize.Serializer;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.concurrent.ConcurrentHashMap;

public class XxlRpcInvokerFactory {

    // TODO, init registry-discovery, add reference-bean to spring
    private ServiceRegistry serviceRegistry;

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }


    // ---------------------- pool factory ----------------------

    private static ConcurrentHashMap<String, GenericObjectPool<ClientPooled>> clientPoolMap =
            new ConcurrentHashMap<String, GenericObjectPool<ClientPooled>>();
    public static GenericObjectPool<ClientPooled> getPool(String address, Serializer serializer, Class<? extends ClientPooled> clientPoolImpl) throws Exception {

        // get pool
        GenericObjectPool<ClientPooled> clientPool = clientPoolMap.get(address);
        if (clientPool != null) {
            return clientPool;
        }

        // parse address
        String[] array = address.split(":");
        String host = array[0];
        int port = Integer.parseInt(array[1]);

        // set pool
        clientPool = new GenericObjectPool(new ClientPoolFactory(host, port, serializer, clientPoolImpl));
        clientPool.setTestOnBorrow(true);
        clientPool.setMaxTotal(2);


        clientPoolMap.put(address, clientPool);
        return clientPool;
    }





}
