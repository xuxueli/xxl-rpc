package com.xxl.rpc.core.register.impl;

import com.xxl.rpc.core.boot.XxlRpcBootstrap;
import com.xxl.rpc.core.register.Register;
import com.xxl.rpc.core.register.entity.RegisterInstance;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * application registry for "local"
 *
 * @author xuxueli 2018-10-17
 */
public class LocalRegister extends Register {

    /**
     * registry data
     */
    private volatile Map<String, TreeSet<RegisterInstance>> registryData;

    public LocalRegister() {
        registryData = new ConcurrentHashMap<>();
    }
    public LocalRegister(Map<String, TreeSet<RegisterInstance>> initRegistryData) {
        this.registryData = initRegistryData;
    }

    /**
     * @param rpcBootstrap ignore, not use
     */
    @Override
    public void start(final XxlRpcBootstrap rpcBootstrap) {
        if (registryData == null) {
            registryData = new ConcurrentHashMap<>();
        }
    }

    @Override
    public void stop() {
        registryData.clear();
    }
    @Override
    public boolean register(RegisterInstance instance) {
        // valid
        if (instance==null
                || instance.getAppname()==null
                || instance.getIp()==null
                || instance.getPort()<=0) {
            return false;
        }

        // do
        Set<RegisterInstance> instances = registryData.computeIfAbsent(instance.getAppname(), k -> new TreeSet<>());
        instances.add(instance);
        return true;
    }

    @Override
    public boolean unregister(RegisterInstance instance) {
        // valid
        if (instance==null || instance.getAppname()==null) {
            return false;
        }
        // do
        if (registryData.containsKey(instance.getAppname())) {
            registryData.get(instance.getAppname()).remove(instance);
        }
        return true;
    }

    @Override
    public Map<String, TreeSet<RegisterInstance>> discovery(Set<String> appnameList){
        // valid
        if (appnameList==null || appnameList.isEmpty()) {
            return null;
        }
        // do
        Map<String, TreeSet<RegisterInstance>> resp = new HashMap<>();
        for (String appname: appnameList){
            resp.put(appname, registryData.get(appname));
        }
        return resp;
    }

    @Override
    public TreeSet<RegisterInstance> discovery(String appname) {
        // valid
        if (appname==null || appname.isEmpty()) {
            return null;
        }
        // do
        return registryData.get(appname);
    }

}
