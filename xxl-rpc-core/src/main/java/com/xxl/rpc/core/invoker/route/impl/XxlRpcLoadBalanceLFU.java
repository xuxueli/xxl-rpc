package com.xxl.rpc.core.invoker.route.impl;

import com.xxl.rpc.core.invoker.route.XxlRpcLoadBalance;
import com.xxl.rpc.core.register.entity.RegisterInstance;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * lru
 *
 * @author xuxueli 2018-12-04
 */
public class XxlRpcLoadBalanceLFU extends XxlRpcLoadBalance {

    private ConcurrentHashMap<String, ConcurrentHashMap<RegisterInstance, AtomicInteger>> frequencyStore = new ConcurrentHashMap<>();
    private long CACHE_VALID_TIME = 0;

    public RegisterInstance doRoute(String serviceKey, TreeSet<RegisterInstance> instanceTreeSet) {

        // cache clear
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            frequencyStore.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 2*60*60*1000;   // 2H
        }

        // init lru-item by serviceKey
        ConcurrentHashMap<RegisterInstance, AtomicInteger> frequencyItem = frequencyStore.get(serviceKey);     // Key排序可以用TreeMap+构造入参Compare；Value排序暂时只能通过ArrayList；
        if (frequencyItem == null) {
            frequencyItem = new ConcurrentHashMap<>();
            frequencyStore.putIfAbsent(serviceKey, frequencyItem);   // 避免重复覆盖
        }

        // set new instance
        for (RegisterInstance instance: instanceTreeSet) {
            if (!frequencyItem.containsKey(instance) || frequencyItem.get(instance).intValue() > 1000000 ) {
                frequencyItem.put(instance, new AtomicInteger(0));
            }
        }

        // remove old
        List<RegisterInstance> delKeys = new ArrayList<>();
        for (RegisterInstance existKey: frequencyItem.keySet()) {
            if (!instanceTreeSet.contains(existKey)) {
                delKeys.add(existKey);
            }
        }
        if (!delKeys.isEmpty()) {
            for (RegisterInstance delKey: delKeys) {
                frequencyItem.remove(delKey);
            }
        }

        // load least userd count address
        List<Map.Entry<RegisterInstance, AtomicInteger>> lfuItemList = new ArrayList<>(frequencyItem.entrySet());
        lfuItemList.sort(Comparator.comparingInt(o -> o.getValue().intValue()));
        Map.Entry<RegisterInstance, AtomicInteger> addressItem = lfuItemList.get(0);

        // update count
        RegisterInstance minAddress = addressItem.getKey();
        addressItem.getValue().incrementAndGet();   // +1

        return minAddress;
    }

    @Override
    public RegisterInstance route(String serviceKey, TreeSet<RegisterInstance> instanceTreeSet) {
        // lfu （serviceKey维度，频率最低节点最优先）
        return doRoute(serviceKey, instanceTreeSet);
    }

}
