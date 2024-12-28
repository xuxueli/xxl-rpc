package com.xxl.rpc.core.invoker.route.impl;

import com.xxl.rpc.core.invoker.route.XxlRpcLoadBalance;
import com.xxl.rpc.core.register.entity.RegisterInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

/**
 * random
 *
 * @author xuxueli 2018-12-04
 */
public class XxlRpcLoadBalanceRandom extends XxlRpcLoadBalance {

    private Random random = new Random();

    @Override
    public RegisterInstance route(String serviceKey, TreeSet<RegisterInstance> instanceTreeSet){
        List<RegisterInstance> list = new ArrayList<>(instanceTreeSet);

        // random（随机）
        int randomIndex = new Random().nextInt(list.size());
        return list.get(randomIndex);
    }

}
