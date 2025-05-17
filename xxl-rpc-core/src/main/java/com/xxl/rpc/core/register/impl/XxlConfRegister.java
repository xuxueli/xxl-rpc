package com.xxl.rpc.core.register.impl;

import com.xxl.rpc.core.boot.XxlRpcBootstrap;
import com.xxl.rpc.core.register.Register;
import com.xxl.rpc.core.register.entity.RegisterInstance;
import com.xxl.rpc.core.register.impl.openapi.RegisterOpenApiTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * registry with xxl-conf
 *
 * @author xuxueli 2018-11-30
 */
public class XxlConfRegister extends Register {
    private static Logger logger = LoggerFactory.getLogger(XxlConfRegister.class);

    /**
     * address
     */
    private volatile String address;
    /**
     * access token
     */
    private volatile String accessToken;

    /**
     * xxlRpcBootstrap
     */
    private XxlRpcBootstrap xxlRpcBootstrap;

    /**
     * addressList (general)
     */
    private List<String> addressList = new ArrayList<>();

    public String loadAddress() {
        String addressTmp = addressList.size()>1
                ? addressList.get(ThreadLocalRandom.current().nextInt(addressList.size()))
                : addressList.get(0);
        return addressTmp;
    }

    public XxlConfRegister() {
    }
    public XxlConfRegister(String address, String accessToken) {
        this.address = address;
        this.accessToken = accessToken;

        // address
        if (!address.contains(",")) {
            addressList.add(address);
        } else {
            addressList.addAll(
                    Arrays.stream(address.split(","))
                            .filter(item -> !item.trim().isEmpty())
                            .collect(Collectors.toList()));
        }
    }


    /**
     * registry data
     */
    private volatile TreeSet<RegisterInstance> registryInstanceListStore = new TreeSet<>();//new ConcurrentSkipListSet<>();

    /**
     * discovery data
     *      key：appname
     *      value：TreeSet<RegisterInstance>
     */
    private volatile Map<String, TreeSet<RegisterInstance>> discoveryAppnameStore = new ConcurrentHashMap<>();

    /**
     * discovery data-md5
     */
    private volatile ConcurrentMap<String, String> discoveryAppnameMd5Store = new ConcurrentHashMap<>();

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
    public void start(XxlRpcBootstrap rpcBootstrap) {
        // base param
        this.xxlRpcBootstrap = rpcBootstrap;

        // valid
        if (addressList == null || addressList.isEmpty()) {
            logger.info(">>>>>>>>>>> xxl-rpc, XxlRpcRegistry start fail, address is null.");
            return;
        }

        // 1、registryThread
        registryThread = startThread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {
                        if (!registryInstanceListStore.isEmpty()) {

                            // 全量注册：次/30s
                            for (RegisterInstance instance : registryInstanceListStore){
                                try {

                                    // register
                                    RegisterOpenApiTool.RegisterInstance instanceTemp = new RegisterOpenApiTool.RegisterInstance(
                                            instance.getAppname(),
                                            instance.getIp(),
                                            instance.getPort(),
                                            instance.getExtendInfo());
                                    RegisterOpenApiTool.OpenApiResponse openApiResponse = RegisterOpenApiTool.register(loadAddress(), accessToken, xxlRpcBootstrap.getBaseConfig().getEnv(), instanceTemp);

                                    logger.info(">>>>>>>>>>> xxl-rpc, registryThread-register {}, instance:{}, openApiResponse:{}", openApiResponse.isSuccess()?"success":"fail",instance, openApiResponse);
                                } catch (Throwable e) {
                                    logger.error(">>>>>>>>>>> xxl-rpc, registryThread-register error:{}", e.getMessage(), e);
                                }
                            }

                        }
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-rpc, registryThread-register error:{}", e.getMessage(), e);
                        }
                    }
                    try {
                        // avoid too fast
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
                    long start = System.currentTimeMillis();
                    try {

                        if (!discoveryAppnameStore.isEmpty()) {
                            // 1、全量服务发现：次/30s
                            doDiscoveryAndRefresh(discoveryAppnameStore.keySet());

                            // 2、增量服务发现：long-polling/实时监听；
                            RegisterOpenApiTool.OpenApiResponse openApiResponse = RegisterOpenApiTool.monitor(
                                    loadAddress(),
                                    accessToken,
                                    xxlRpcBootstrap.getBaseConfig().getEnv(),
                                    new ArrayList<>(discoveryAppnameStore.keySet()),
                                    REGISTRY_BEAT_TIME);

                        }

                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-rpc, XxlRpcRegister-discoveryThread error:{}", e.getMessage(), e);
                        }
                    }
                    try {
                        // avoid too fast
                        long IntervalTime = System.currentTimeMillis() - start;
                        if (IntervalTime < 3000L) {
                            TimeUnit.MILLISECONDS.sleep(3000L);
                        }
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
        // 1、udpate registry-store
        registryInstanceListStore.add(instance);

        // 2、增量注册：立即触发
        try {

            // register
            RegisterOpenApiTool.RegisterInstance instanceTemp = new RegisterOpenApiTool.RegisterInstance(
                    instance.getAppname(),
                    instance.getIp(),
                    instance.getPort(),
                    instance.getExtendInfo());
            RegisterOpenApiTool.OpenApiResponse openApiResponse = RegisterOpenApiTool.register(loadAddress(), accessToken, xxlRpcBootstrap.getBaseConfig().getEnv(), instanceTemp);

            logger.info(">>>>>>>>>>> xxl-rpc, XxlRpcRegister-register {}, instance:{}, openApiResponse:{}", openApiResponse.isSuccess()?"success":"fail", instanceTemp, openApiResponse);
            return openApiResponse.isSuccess();
        } catch (Throwable e) {
            logger.error(">>>>>>>>>>> xxl-rpc, XxlRpcRegister-register error:{}", e.getMessage(), e);
        }

        return false;
    }

    @Override
    public boolean unregister(RegisterInstance instance) {
        // 1、udpate registry-store
        registryInstanceListStore.remove(instance);

        // 2、增量注册注销：立即触发
        try {

            // register
            RegisterOpenApiTool.RegisterInstance instanceTemp = new RegisterOpenApiTool.RegisterInstance(
                    instance.getAppname(),
                    instance.getIp(),
                    instance.getPort(),
                    instance.getExtendInfo());
            RegisterOpenApiTool.OpenApiResponse openApiResponse = RegisterOpenApiTool.unregister(loadAddress(), accessToken, xxlRpcBootstrap.getBaseConfig().getEnv(), instanceTemp);

            logger.info(">>>>>>>>>>> xxl-rpc, XxlRpcRegister-unregister {}, instance:{}, openApiResponse:{}", openApiResponse.isSuccess()?"success":"fail", instanceTemp, openApiResponse);
            return openApiResponse.isSuccess();
        } catch (Throwable e) {
            logger.error(">>>>>>>>>>> xxl-rpc, XxlRpcRegister-unregister error:{}", e.getMessage(), e);
        }

        return false;
    }

    @Override
    public Map<String, TreeSet<RegisterInstance>> discovery(Set<String> appnameList) {
        // valid
        if (appnameList == null || appnameList.isEmpty()) {
            return new HashMap<>();
        }

        // 1、discovery local, filter not-found
        Map<String, TreeSet<RegisterInstance>> result = new HashMap<>();
        Set<String> appnameNotFound = new HashSet<>();
        for (String appname : appnameList) {
            if (discoveryAppnameStore.containsKey(appname)) {
                result.put(appname, discoveryAppnameStore.get(appname));
            } else {
                appnameNotFound.add(appname);
            }
        }

        // 2、增量服务发现：本地不存在信息，立即触发查询
        if (!appnameNotFound.isEmpty()) {
            Map<String, TreeSet<RegisterInstance>> discoveryResult = doDiscoveryAndRefresh(appnameNotFound);
            if (discoveryResult != null) {
                result.putAll(discoveryResult);
            }
        }

        return result;
    }

    private Map<String, TreeSet<RegisterInstance>> doDiscoveryAndRefresh(Set<String> appnameList) {
        try {
            // discovery
            List<String> appnameListTemp = new ArrayList<String>(appnameList);
            RegisterOpenApiTool.DiscoveryResponse discoveryResponse = RegisterOpenApiTool.discovery(
                    loadAddress(),
                    accessToken,
                    xxlRpcBootstrap.getBaseConfig().getEnv(),
                    appnameListTemp,
                    false);

            // parse result
            if (!discoveryResponse.isSuccess()) {
                logger.info(">>>>>>>>>>> xxl-rpc, XxlRpcRegister-doDiscoveryAndRefresh {}, appnameList:{}, discoveryResponse:{}", discoveryResponse.isSuccess()?"success":"fail",appnameList, discoveryResponse);
            } else {
                logger.debug(">>>>>>>>>>> xxl-rpc, XxlRpcRegister-doDiscoveryAndRefresh {}, appnameList:{}, discoveryResponse:{}", discoveryResponse.isSuccess()?"success":"fail",appnameList, discoveryResponse);
            }

            if (discoveryResponse.isSuccess() && discoveryResponse.getDiscoveryData()!=null) {

                // result param
                Map<String, TreeSet<RegisterInstance>> result = new HashMap<>();
                Map<String, String> resultMd5 = new HashMap<>();

                // parse
                for (String appname : discoveryResponse.getDiscoveryData().keySet()) {
                    // remote data
                    List<RegisterOpenApiTool.InstanceCacheDTO> instanceCacheDTOS = discoveryResponse.getDiscoveryData().get(appname);
                    String registerInstancesMd5 = discoveryResponse.getDiscoveryDataMd5().get(appname);

                    TreeSet<RegisterInstance> registerInstances = new TreeSet<>();
                    if (instanceCacheDTOS != null) {
                        for (RegisterOpenApiTool.InstanceCacheDTO instanceCacheDTO : instanceCacheDTOS) {
                            registerInstances.add(new RegisterInstance(instanceCacheDTO.getEnv(), appname, instanceCacheDTO.getIp(), instanceCacheDTO.getPort(), instanceCacheDTO.getExtendInfo()));
                        }
                    } else {
                        registerInstances = new TreeSet<>();   // cache none
                    }

                    // fill new data
                    result.put(appname, registerInstances);
                    resultMd5.put(appname, registerInstancesMd5);

                    // find diff
                    if (!resultMd5.get(appname).equals(discoveryAppnameMd5Store.get(appname))) {
                        logger.info(">>>>>>>>>>> xxl-rpc, XxlRpcRegister-doDiscoveryAndRefresh success, appname:{}, registerInstances:{}", appname , registerInstances);
                    }
                }

                // fill local data
                discoveryAppnameStore.putAll(result);
                discoveryAppnameMd5Store.putAll(resultMd5);

                return result;
            }
        } catch (Throwable e) {
            logger.error(">>>>>>>>>>> xxl-rpc, XxlRpcRegister-doDiscovery error:{}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public TreeSet<RegisterInstance> discovery(String appname) {
        Map<String, TreeSet<RegisterInstance>> result = discovery(new HashSet<>(Arrays.asList(appname)));
        return result.get(appname);
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
