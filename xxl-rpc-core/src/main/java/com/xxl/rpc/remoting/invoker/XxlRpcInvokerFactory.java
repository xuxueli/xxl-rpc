package com.xxl.rpc.remoting.invoker;

import com.xxl.rpc.registry.ServiceRegistry;
import com.xxl.rpc.remoting.net.params.RpcCallbackFuture;
import com.xxl.rpc.remoting.net.pool.ClientPoolFactory;
import com.xxl.rpc.remoting.net.pool.ClientPooled;
import com.xxl.rpc.serialize.Serializer;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * xxl-rpc invoker factory
 *
 * @author xuxueli 2018-10-19
 */
public class XxlRpcInvokerFactory {


    // ---------------------- config ----------------------

    private Class<? extends ServiceRegistry> serviceRegistryClass;          // class.forname
    private Map<String, String> serviceRegistryParam;


    public XxlRpcInvokerFactory() {
    }
    public XxlRpcInvokerFactory(Class<? extends ServiceRegistry> serviceRegistryClass, Map<String, String> serviceRegistryParam) {
        this.serviceRegistryClass = serviceRegistryClass;
        this.serviceRegistryParam = serviceRegistryParam;
    }

    // ---------------------- start / stop ----------------------

    public void start() throws Exception {
        // start registry
        if (serviceRegistryClass != null) {
            serviceRegistry = serviceRegistryClass.newInstance();
            serviceRegistry.start(serviceRegistryParam);
        }
    }

    public void  stop() throws Exception {
        // stop registry
        if (serviceRegistry != null) {
            serviceRegistry.stop();
        }
    }

    // ---------------------- service registry (static) ----------------------

    private static ServiceRegistry serviceRegistry;
    public static ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }


    // ---------------------- client pool (static) ----------------------

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


    // ---------------------- future pool (static) ----------------------

    public static ConcurrentMap<String, RpcCallbackFuture> futurePool = new ConcurrentHashMap<String, RpcCallbackFuture>();

    public static void setInvokerFuture(String key, RpcCallbackFuture future){
        futurePool.put(key, future);
    }
    public static void removeInvokerFuture(String key){
        futurePool.remove(key);
    }
    public static RpcCallbackFuture getInvokerFuture(String key){
        return futurePool.get(key);
    }



}
