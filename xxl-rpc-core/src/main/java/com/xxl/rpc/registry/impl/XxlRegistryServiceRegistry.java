package com.xxl.rpc.registry.impl;

import com.xxl.registry.client.XxlRegistryClient;
import com.xxl.registry.client.model.XxlRegistryParam;
import com.xxl.rpc.registry.ServiceRegistry;

import java.util.*;

/**
 * service registry for "xxl-registry v1.0.0"
 *
 * @author xuxueli 2018-11-30
 */
public class XxlRegistryServiceRegistry extends ServiceRegistry {

    public static final String XXL_REGISTRY_ADDRESS = "XXL_REGISTRY_ADDRESS";
    public static final String ENV = "ENV";

    private XxlRegistryClient xxlRegistryClient;

    @Override
    public void start(Map<String, String> param) {
        String xxlRegistryAddress = param.get(XXL_REGISTRY_ADDRESS);
        String env = param.get(ENV);

        xxlRegistryClient = new XxlRegistryClient(xxlRegistryAddress, "xxl-rpc", env);
    }

    @Override
    public void stop() {
        if (xxlRegistryClient != null) {
            xxlRegistryClient.stop();
        }
    }

    @Override
    public boolean registry(Set<String> keys, String value) {
        if (keys==null || keys.size() == 0 || value == null) {
            return false;
        }

        // init
        List<XxlRegistryParam> registryParamList = new ArrayList<>();
        for (String key:keys) {
            registryParamList.add(new XxlRegistryParam(key, value));
        }

        return xxlRegistryClient.registry(registryParamList);
    }

    @Override
    public boolean remove(Set<String> keys, String value) {
        if (keys==null || keys.size() == 0 || value == null) {
            return false;
        }

        // init
        List<XxlRegistryParam> registryParamList = new ArrayList<>();
        for (String key:keys) {
            registryParamList.add(new XxlRegistryParam(key, value));
        }

        return xxlRegistryClient.remove(registryParamList);
    }

    @Override
    public Map<String, TreeSet<String>> discovery(Set<String> keys) {
        return xxlRegistryClient.discovery(keys);
    }

    @Override
    public TreeSet<String> discovery(String key) {
        return xxlRegistryClient.discovery(key);
    }

}
