package com.xxl.rpc.core.invoker.route.impl;

import com.xxl.rpc.core.invoker.route.XxlRpcLoadBalance;
import com.xxl.rpc.core.register.entity.RegisterInstance;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * lru
 *
 * @author xuxueli 2018-12-04
 */
public class XxlRpcLoadBalanceLRU extends XxlRpcLoadBalance {

    private ConcurrentMap<String, LinkedHashMap<RegisterInstance, Boolean>> lruStore = new ConcurrentHashMap<>();
    private long CACHE_VALID_TIME = 0;

    public RegisterInstance doRoute(String serviceKey, TreeSet<RegisterInstance> instanceTreeSet) {

        // cache clear
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            lruStore.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 2*60*60*1000;   // 2H
        }

        // init lru
        LinkedHashMap<RegisterInstance, Boolean> lruItem = lruStore.get(serviceKey);
        if (lruItem == null) {
            /**
             * LinkedHashMap
             *      a、accessOrder：ture=访问顺序排序（get/put时排序）/ACCESS-LAST；false=插入顺序排期/FIFO；
             *      b、removeEldestEntry：新增元素时将会调用，返回true时会删除最老元素；可封装LinkedHashMap并重写该方法，比如定义最大容量，超出是返回true即可实现固定长度的LRU算法；
             */
            lruItem = new LinkedHashMap<RegisterInstance, Boolean>(16, 0.75f, true){
                @Override
                protected boolean removeEldestEntry(Map.Entry<RegisterInstance, Boolean> eldest) {
                    if(super.size() > 1000){
                        return true;
                    }else{
                        return false;
                    }
                }
            };
            lruStore.putIfAbsent(serviceKey, lruItem);      // 避免重复覆盖
        }

        // put new
        for (RegisterInstance instance: instanceTreeSet) {
            if (!lruItem.containsKey(instance)) {
                lruItem.put(instance, true);
            }
        }
        // remove old
        List<RegisterInstance> delKeys = new ArrayList<>();
        for (RegisterInstance existKey: lruItem.keySet()) {
            if (!instanceTreeSet.contains(existKey)) {
                delKeys.add(existKey);
            }
        }
        if (!delKeys.isEmpty()) {
            for (RegisterInstance delKey: delKeys) {
                lruItem.remove(delKey);
            }
        }

        // load
        RegisterInstance eldestKey = lruItem.entrySet().iterator().next().getKey();     // FIFO，最旧的数据
        return eldestKey;
    }

    @Override
    public RegisterInstance route(String serviceKey, TreeSet<RegisterInstance> instanceTreeSet) {
        // lru （serviceKey维度，最久未使用节点）
        return doRoute(serviceKey, instanceTreeSet);
    }

}
