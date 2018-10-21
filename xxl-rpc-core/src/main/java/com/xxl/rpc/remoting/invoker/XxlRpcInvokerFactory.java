package com.xxl.rpc.remoting.invoker;

import com.xxl.rpc.registry.ServiceRegistry;
import com.xxl.rpc.remoting.net.params.XxlRpcFutureResponse;
import com.xxl.rpc.remoting.net.pool.ClientPoolFactory;
import com.xxl.rpc.remoting.net.pool.ClientPooled;
import com.xxl.rpc.serialize.Serializer;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.eclipse.jetty.client.HttpClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * xxl-rpc invoker factory, init service-registry
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

        // init jetty http client
        getJettyHttpClient();
    }

    public void  stop() throws Exception {
        // stop registry
        if (serviceRegistry != null) {
            serviceRegistry.stop();
        }

        //  stop jetty http client
        stopJettyHttpClient();
    }

    // ---------------------- service registry (static) ----------------------

    private static ServiceRegistry serviceRegistry;
    public static ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }


    // ---------------------- client pool (static) ----------------------

    private static ConcurrentHashMap<String, GenericObjectPool<ClientPooled>> clientPoolMap = new ConcurrentHashMap<String, GenericObjectPool<ClientPooled>>();
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
        clientPool = new GenericObjectPool<ClientPooled>(new ClientPoolFactory(host, port, serializer, clientPoolImpl));
        clientPool.setTestOnBorrow(true);
        clientPool.setMaxTotal(2);


        clientPoolMap.put(address, clientPool);
        return clientPool;
    }


    // ---------------------- future pool (static) ----------------------

    private static ConcurrentMap<String, XxlRpcFutureResponse> futureResponsePool = new ConcurrentHashMap<String, XxlRpcFutureResponse>();

    public static void setInvokerFuture(String requestId, XxlRpcFutureResponse futureResponse){
        futureResponsePool.put(requestId, futureResponse);
    }
    public static void removeInvokerFuture(String requestId){
        futureResponsePool.remove(requestId);
    }
    public static XxlRpcFutureResponse getInvokerFuture(String requestId){
        return futureResponsePool.get(requestId);
    }


    // ---------------------- jetty client(static) ----------------------
    private static HttpClient jettyHttpClient = null;
    public static HttpClient getJettyHttpClient() throws Exception {
        if (jettyHttpClient != null) {
            return jettyHttpClient;
        }

        // httpclient init
        jettyHttpClient = new HttpClient();
        jettyHttpClient.setFollowRedirects(false);	                // Configure HttpClient, for example:
        jettyHttpClient.setMaxConnectionsPerDestination(1000);	    // TODO, more config
        jettyHttpClient.start();						            // Start HttpClient

        return jettyHttpClient;
    }
    public static void stopJettyHttpClient() throws Exception {
        if (jettyHttpClient != null) {
            jettyHttpClient.stop();
        }
    }

}
