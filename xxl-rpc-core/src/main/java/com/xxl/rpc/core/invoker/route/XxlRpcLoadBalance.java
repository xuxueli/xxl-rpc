package com.xxl.rpc.core.invoker.route;

import com.xxl.rpc.core.register.entity.RegisterInstance;

import java.util.TreeSet;

/**
 * 分组下机器地址相同，不同JOB均匀散列在不同机器上，保证分组下机器分配JOB平均；且每个JOB固定调度其中一台机器；
 *      a、virtual node：解决不均衡问题
 *      b、hash method replace hashCode：String的hashCode可能重复，需要进一步扩大hashCode的取值范围
 *
 * @author xuxueli 2018-12-04
 */
public abstract class XxlRpcLoadBalance {

    public abstract RegisterInstance route(String serviceKey, TreeSet<RegisterInstance> instanceTreeSet);

}
