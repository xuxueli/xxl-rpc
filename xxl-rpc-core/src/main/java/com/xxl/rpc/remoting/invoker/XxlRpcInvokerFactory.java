package com.xxl.rpc.remoting.invoker;

import com.xxl.rpc.registry.ServiceRegistry;
import com.xxl.rpc.remoting.net.impl.jetty.client.JettyClient;
import com.xxl.rpc.remoting.net.pool.ClientPooled;

import java.util.Map;

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

        // start jetty httpclient
        JettyClient.getJettyHttpClient();
    }

    public void  stop() throws Exception {
        // stop registry
        if (serviceRegistry != null) {
            serviceRegistry.stop();
        }

        // stop jetty client pool
        JettyClient.stopJettyHttpClient();

        // stop tcp client pool
        ClientPooled.stopPool();
    }

    // ---------------------- service registry (static) ----------------------

    private static ServiceRegistry serviceRegistry;
    public static ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }


}
