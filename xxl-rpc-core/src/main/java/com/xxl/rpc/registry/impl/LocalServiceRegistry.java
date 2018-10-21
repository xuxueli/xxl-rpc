package com.xxl.rpc.registry.impl;

import com.xxl.rpc.registry.ServiceRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * local service registry
 *
 * @author xuxueli 2018-10-17
 */
public class LocalServiceRegistry extends ServiceRegistry {

    /**
     * registry data
     */
    private Map<String, TreeSet<String>> registryData;


    /**
     * @param param ignore, not use
     */
    @Override
    public void start(Map<String, String> param) {
        registryData = new HashMap<String, TreeSet<String>>();
    }

    @Override
    public void stop() {
        registryData.clear();
    }


    @Override
    public boolean registry(String key, String value) {
        if (key==null || key.trim().length()==0 || value==null || value.trim().length()==0) {
            return false;
        }
        TreeSet<String> values = registryData.get(key);
        if (values == null) {
            values = new TreeSet<>();
            registryData.put(key, values);
        }
        values.add(value);
        return true;
    }

    @Override
    public boolean remove(String key, String value) {
        TreeSet<String> values = registryData.get(key);
        if (values != null) {
            values.remove(value);
        }
        return true;
    }

    @Override
    public TreeSet<String> discovery(String key) {
        return registryData.get(key);
    }

}
