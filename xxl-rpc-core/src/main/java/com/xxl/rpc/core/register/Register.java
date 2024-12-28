package com.xxl.rpc.core.register;


import com.xxl.rpc.core.boot.XxlRpcBootstrap;
import com.xxl.rpc.core.register.entity.RegisterInstance;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * application registry
 *
 * @author xuxueli 2018-10-17
 */
public abstract class Register {

    /**
     * start
     *
     * logic：
     *      1、服务注册线程，循环心跳注册，维护 “注册数据（register 提交后才维护）” 在线状态；
     *      2、服务发现线程，long-polling结合轮训，实时感知 “服务注册（discovery 提交后才维护）” 信息；
     *
     * @param factory
     */
    public abstract void start(final XxlRpcBootstrap factory);

    /**
     * stop
     */
    public abstract void stop();

    /**
     * register
     */
    public abstract boolean register(RegisterInstance instance);

    /**
     * unregister
     */
    public abstract boolean unregister(RegisterInstance instance);

    /**
     * discovery appname-list
     *
     * @param   appnameList
     * @return  appname : RegisterInstance - list
     */
    public abstract Map<String, TreeSet<RegisterInstance>> discovery(Set<String> appnameList);

    /**
     * discovery service, for one
     *
     * @param       appname
     * @return      RegisterInstance - list
     */
    public abstract TreeSet<RegisterInstance> discovery(String appname);

}
