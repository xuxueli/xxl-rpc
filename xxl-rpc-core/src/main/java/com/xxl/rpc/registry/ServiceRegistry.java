package com.xxl.rpc.registry;


import com.xxl.rpc.registry.impl.LocalServiceRegistry;
import com.xxl.rpc.registry.impl.ZkServiceRegistry;

import java.util.Map;
import java.util.TreeSet;

/**
 * service registry
 *
 *  /xxl-rpc/dev/
 *              - key01(service01)
 *                  - value01 (ip:port01)
 *                  - value02 (ip:port02)
 *
 * @author xuxueli 2018-10-17
 */
public abstract class ServiceRegistry {

    /**
     * start
     */
    public abstract void start(Map<String, String> param);

    /**
     * start
     */
    public abstract void stop();


    /**
     * registry service
     *
     * @param key       service key
     * @param value     service value/ip:port
     * @return
     */
    public abstract boolean registry(String key, String value);


    /**
     * remove service
     *
     * @param key
     * @param value
     * @return
     */
    public abstract boolean remove(String key, String value);


    /**
     * discovery service
     *
     * @param key   service key
     * @return      service value/ip:port
     */
    public abstract TreeSet<String> discovery(String key);


    public enum ServiceRegistryEnum {
        LOCAL(LocalServiceRegistry.class),
        ZK(ZkServiceRegistry.class);

        public final Class<? extends ServiceRegistry> serviceRegistryClass;
        private ServiceRegistryEnum (Class<? extends ServiceRegistry> serviceRegistryClass) {
            this.serviceRegistryClass = serviceRegistryClass;
        }
        public static ServiceRegistryEnum match(String name, ServiceRegistryEnum defaultServiceRegistry){
            for (ServiceRegistryEnum item : ServiceRegistryEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
            return defaultServiceRegistry;
        }
    }

}
