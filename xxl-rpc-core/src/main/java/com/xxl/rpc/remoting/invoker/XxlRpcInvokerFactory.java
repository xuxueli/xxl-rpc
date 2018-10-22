package com.xxl.rpc.remoting.invoker;

import com.xxl.rpc.registry.ServiceRegistry;
import com.xxl.rpc.remoting.net.params.BaseCallback;

import java.util.ArrayList;
import java.util.List;
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
    }

    public void  stop() throws Exception {
        // stop registry
        if (serviceRegistry != null) {
            serviceRegistry.stop();
        }

        // stop callback
        if (stopCallbackList.size() > 0) {
            for (BaseCallback callback: stopCallbackList) {
                callback.run();
            }
        }

    }


    // ---------------------- service registry (static) ----------------------

    private static ServiceRegistry serviceRegistry;

    public static ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }


    // ---------------------- service registry (static) ----------------------
    private static List<BaseCallback> stopCallbackList = new ArrayList<BaseCallback>();     // JettyClient、ClientPooled

    public static void addStopCallBack(BaseCallback callback){
        stopCallbackList.add(callback);
    }


}
