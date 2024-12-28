package com.xxl.rpc.core.invoker.route.impl;

import com.xxl.rpc.core.invoker.route.XxlRpcLoadBalance;
import com.xxl.rpc.core.register.entity.RegisterInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * round
 *
 * @author xuxueli 2018-12-04
 */
public class XxlRpcLoadBalanceRound extends XxlRpcLoadBalance {

    private ConcurrentMap<String, AtomicInteger> routeCountStore = new ConcurrentHashMap<>();
    private long CACHE_VALID_TIME = 0;
    private int count(String serviceKey) {
        // cache clear
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            routeCountStore.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 2*60*60*1000;   // 2H
        }

        // count++
        AtomicInteger count = routeCountStore.get(serviceKey);
        if (count == null || count.get() > 1000000) {
            // 初始化时主动Random一次，缓解首次压力
            count = new AtomicInteger(new Random().nextInt(1000));
        } else {
            // count++
            count.addAndGet(1);
        }

        routeCountStore.put(serviceKey, count);
        return count.get();
    }

    @Override
    public RegisterInstance route(String serviceKey, TreeSet<RegisterInstance> instanceTreeSet) {
        List<RegisterInstance> list = new ArrayList<>(instanceTreeSet);

        // round （循环：serviceKey维度，针对对应服务节点循环遍历）
        int roundIndex = count(serviceKey)%instanceTreeSet.size();
        return list.get(roundIndex);
    }

}
