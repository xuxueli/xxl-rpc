package com.xxl.rpc.core.register.impl;

import com.xxl.rpc.core.boot.XxlRpcBootstrap;
import com.xxl.rpc.core.register.Register;
import com.xxl.rpc.core.register.entity.RegisterInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * XxlRpcRegister
 *
 * @author xuxueli 2018-11-30
 */
public class XxlRpcRegister extends Register {
    private static Logger logger = LoggerFactory.getLogger(XxlRpcRegister.class);

    /**
     * xxl-rpc-admin config
     */
    private String adminAddress;
    /**
     * access token, for xxl-rpc-admin
     */
    private String accessToken;


    public XxlRpcRegister() {
    }
    public XxlRpcRegister(String adminAddress, String accessToken) {
        this.adminAddress = adminAddress;
        this.accessToken = accessToken;
    }


    /**
     * registry data
     */
    private TreeSet<RegisterInstance> registryInstanceListStore = new TreeSet<>();//new ConcurrentSkipListSet<>();

    /**
     * discovery data
     *      key：appname
     *      value：TreeSet<RegisterInstance>
     */
    private Map<String, TreeSet<RegisterInstance>> discoveryAppnameStore = new ConcurrentHashMap<>();

    /**
     * BeatTime Interval, by second
     */
    public static final int REGISTRY_BEAT_TIME = 30;

    /**
     * thread stop variable
     */
    private volatile boolean toStop = false;

    /**
     * registry Thread
     */
    private Thread registryThread;

    /**
     * discovery Thread
     */
    private Thread discoveryThread;

    @Override
    public void start(XxlRpcBootstrap factory) {
        // valid
        if (adminAddress == null || adminAddress.trim().length() == 0) {
            logger.info(">>>>>>>>>>> xxl-rpc, XxlRpcRegistry start fail, adminAddress is null.");
            return;
        }

        // TODO，启动注册线程、发现线程，维护注册数据、发现数据

        // 1、registryThread
        registryThread = startThread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {

                        if (!registryInstanceListStore.isEmpty()) {
                            // TODO，循环心跳注册，30s/次
                        }

                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-rpc, XxlRpcRegister-registryThread error:{}", e.getMessage(), e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(REGISTRY_BEAT_TIME);
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-rpc, XxlRpcRegister-registryThread error2:{}", e.getMessage(), e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> xxl-rpc, XxlRpcRegister-registryThread finish.");
            }
        }, "xxl-rpc, XxlRpcRegister-registryThread");

        // 2、discoveryThread
        discoveryThread = startThread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {

                        if (!discoveryAppnameStore.isEmpty()) {
                            // TODO，long-polling/实时监听 + 循环全量匹配，30s/次
                        }

                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-rpc, XxlRpcRegister-discoveryThread error:{}", e.getMessage(), e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(REGISTRY_BEAT_TIME);
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-rpc, XxlRpcRegister-discoveryThread error2:{}", e.getMessage(), e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> xxl-rpc, XxlRpcRegister-discoveryThread finish.");
            }
        }, "xxl-rpc, XxlRpcRegister-discoveryThread");

    }

    @Override
    public void stop() {
        // mark stop
        toStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }

        // 1、registryThread
        stopThread(registryThread);

        // 2、discoveryThread
        stopThread(discoveryThread);
    }


    @Override
    public boolean register(RegisterInstance instance) {
        // TODO, ；纳入本地注册Store，立即发起一次注册；
        // 工具类，抽象到 SDK 里；
        return false;
    }

    @Override
    public boolean unregister(RegisterInstance instance) {
        // TODO，；纳入本地注册Store，立即发起一次注销；
        return false;
    }

    @Override
    public Map<String, TreeSet<RegisterInstance>> discovery(Set<String> appnameList) {
        // TODO；本地没有Key，立即发起远程查询，本地写None；
        return Collections.emptyMap();
    }

    @Override
    public TreeSet<RegisterInstance> discovery(String appname) {
        // TODO，本地没有Key，立即发起远程查询，本地写None；
        return new TreeSet<>();
    }

    // ---------------------- util ----------------------

    /**
     * start thread
     *
     * @param runnable
     * @param name
     * @return
     */
    public static Thread startThread(Runnable runnable, String name) {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.setName(name);
        thread.start();
        return thread;
    }

    /**
     * stop thread
     *
     * @param thread
     */
    public static void stopThread(Thread thread) {
        if (thread.getState() != Thread.State.TERMINATED){
            // interrupt and wait
            thread.interrupt();
            try {
                thread.join();
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
