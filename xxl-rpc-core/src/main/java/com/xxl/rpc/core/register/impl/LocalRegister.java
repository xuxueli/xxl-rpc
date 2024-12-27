package com.xxl.rpc.core.register.impl;

import com.xxl.rpc.core.factory.XxlRpcFactory;
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
    private volatile Map<String, Set<RegisterInstance>> registryData;

    public LocalRegister() {
    }
    public LocalRegister(Map<String, Set<RegisterInstance>> initRegistryData) {
        this.registryData = initRegistryData;
    }

    /**
     * @param factory ignore, not use
     */
    @Override
    public void start(final XxlRpcFactory factory) {
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
    public Map<String, Set<RegisterInstance>> discovery(Set<String> appnameList){
        // valid
        if (appnameList==null || appnameList.isEmpty()) {
            return null;
        }
        // do
        Map<String, Set<RegisterInstance>> resp = new HashMap<>();
        for (String appname: appnameList){
            resp.put(appname, registryData.get(appname));
        }
        return resp;
    }

    @Override
    public Set<RegisterInstance> discovery(String appname) {
        // valid
        if (appname==null || appname.isEmpty()) {
            return null;
        }
        // do
        return registryData.get(appname);
    }

}
