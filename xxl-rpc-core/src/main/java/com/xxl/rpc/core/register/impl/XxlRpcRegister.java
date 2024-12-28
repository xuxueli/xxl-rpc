package com.xxl.rpc.core.register.impl;

import com.alibaba.fastjson2.JSON;
import com.xxl.rpc.core.boot.XxlRpcBootstrap;
import com.xxl.rpc.core.register.Register;
import com.xxl.rpc.core.register.entity.RegisterInstance;
import com.xxl.rpc.core.register.impl.dto.XxlRpcRegisterDTO;
import com.xxl.tool.encrypt.Md5Tool;
import com.xxl.tool.net.HttpTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
    private volatile String adminAddress;
    /**
     * access token, for xxl-rpc-admin
     */
    private volatile String accessToken;

    /**
     * xxlRpcBootstrap
     */
    private XxlRpcBootstrap xxlRpcBootstrap;


    public XxlRpcRegister() {
    }
    public XxlRpcRegister(String adminAddress, String accessToken) {
        this.adminAddress = adminAddress;
        this.accessToken = accessToken;
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
        if (adminAddress == null || adminAddress.trim().length() == 0) {
            logger.info(">>>>>>>>>>> xxl-rpc, XxlRpcRegistry start fail, adminAddress is null.");
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
                                    XxlRpcRegisterDTO.RegisterRequest request = new XxlRpcRegisterDTO.RegisterRequest();
                                    request.setAccessToken(accessToken);
                                    request.setEnv(rpcBootstrap.getBaseConfig().getEnv());
                                    request.setInstance(new XxlRpcRegisterDTO.RegisterInstance(instance.getAppname(), instance.getIp(), instance.getPort(), instance.getExtendInfo()));

                                    String responseBody = HttpTool.postBody(adminAddress + "/openapi/register",
                                            JSON.toJSONString(request),
                                            null,
                                            3000);

                                    XxlRpcRegisterDTO.OpenApiResponse openApiResponse = JSON.parseObject(responseBody, XxlRpcRegisterDTO.OpenApiResponse.class);
                                    logger.info(">>>>>>>>>>> xxl-rpc, registryThread-register {}, instance:{}, responseBody:{}", openApiResponse.isSuccess()?"success":"fail",instance, responseBody);
                                } catch (Exception e) {
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
                    try {

                        if (!discoveryAppnameStore.isEmpty()) {
                            // 1、全量服务发现：次/30s
                            Map<String, TreeSet<RegisterInstance>> discoveryResult = doDiscovery(discoveryAppnameStore.keySet());

                            // 2、增量服务发现：long-polling/实时监听 TODO

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
        // 1、udpate registry-store
        registryInstanceListStore.add(instance);

        // 2、增量注册：立即触发
        try {
            XxlRpcRegisterDTO.RegisterRequest request = new XxlRpcRegisterDTO.RegisterRequest();
            request.setAccessToken(accessToken);
            request.setEnv(xxlRpcBootstrap.getBaseConfig().getEnv());
            request.setInstance(new XxlRpcRegisterDTO.RegisterInstance(instance.getAppname(), instance.getIp(), instance.getPort(), instance.getExtendInfo()));

            String responseBody = HttpTool.postBody(adminAddress + "/openapi/register",
                    JSON.toJSONString(request),
                    null,
                    3000);

            XxlRpcRegisterDTO.OpenApiResponse openApiResponse = JSON.parseObject(responseBody, XxlRpcRegisterDTO.OpenApiResponse.class);
            logger.info(">>>>>>>>>>> xxl-rpc, XxlRpcRegister-register {}, instance:{}, responseBody:{}", openApiResponse.isSuccess()?"success":"fail",instance, responseBody);
            return openApiResponse.isSuccess();
        } catch (Exception e) {
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
            XxlRpcRegisterDTO.RegisterRequest request = new XxlRpcRegisterDTO.RegisterRequest();
            request.setAccessToken(accessToken);
            request.setEnv(xxlRpcBootstrap.getBaseConfig().getEnv());
            request.setInstance(new XxlRpcRegisterDTO.RegisterInstance(instance.getAppname(), instance.getIp(), instance.getPort(), instance.getExtendInfo()));

            String responseBody = HttpTool.postBody(adminAddress + "/openapi/unregister",
                    JSON.toJSONString(request),
                    null,
                    3000);

            XxlRpcRegisterDTO.OpenApiResponse openApiResponse = JSON.parseObject(responseBody, XxlRpcRegisterDTO.OpenApiResponse.class);
            logger.info(">>>>>>>>>>> xxl-rpc, XxlRpcRegister-unregister {}, instance:{}, responseBody:{}", openApiResponse.isSuccess()?"success":"fail",instance, responseBody);
            return openApiResponse.isSuccess();
        } catch (Exception e) {
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
            Map<String, TreeSet<RegisterInstance>> discoveryResult = doDiscovery(appnameNotFound);
            if (discoveryResult != null) {
                result.putAll(discoveryResult);
            }
        }

        return result;
    }

    private Map<String, TreeSet<RegisterInstance>> doDiscovery(Set<String> appnameList) {
        try {
            // request
            XxlRpcRegisterDTO.DiscoveryRequest request = new XxlRpcRegisterDTO.DiscoveryRequest();
            request.setAccessToken(accessToken);
            request.setEnv(xxlRpcBootstrap.getBaseConfig().getEnv());
            request.setAppnameList(new ArrayList<String>(appnameList));
            request.setSimpleQuery(false);

            String responseBody = HttpTool.postBody(adminAddress + "/openapi/discovery",
                    JSON.toJSONString(request),
                    null,
                    3000
            );
            XxlRpcRegisterDTO.OpenApiResponse<XxlRpcRegisterDTO.DiscoveryResponse> openApiResponse = JSON.parseObject(responseBody, XxlRpcRegisterDTO.OpenApiResponse.class);

            // parse result
            logger.info(">>>>>>>>>>> xxl-rpc, XxlRpcRegister-doDiscovery {}, appnameList:{}, responseBody:{}", openApiResponse.isSuccess()?"success":"fail",appnameList, responseBody);
            if (openApiResponse.isSuccess() && openApiResponse.getData()!=null) {

                // result param
                Map<String, TreeSet<RegisterInstance>> result = new HashMap<>();
                Map<String, String> resultMd5 = new HashMap<>();

                // parse
                for (String appname : openApiResponse.getData().getDiscoveryData().keySet()) {
                    // remote data
                    List<XxlRpcRegisterDTO.InstanceCacheDTO> instanceCacheDTOS = openApiResponse.getData().getDiscoveryData().get(appname);
                    String registerInstancesMd5 =openApiResponse.getData().getDiscoveryDataMd5().get(appname);

                    TreeSet<RegisterInstance> registerInstances = new TreeSet<>();
                    if (instanceCacheDTOS != null) {
                        for (XxlRpcRegisterDTO.InstanceCacheDTO instanceCacheDTO : instanceCacheDTOS) {
                            registerInstances.add(new RegisterInstance(instanceCacheDTO.getEnv(), appname, instanceCacheDTO.getIp(), instanceCacheDTO.getPort(), instanceCacheDTO.getExtendInfo()));
                        }
                    } else {
                        registerInstances = new TreeSet<>();   // cache none
                    }
                    result.put(appname, registerInstances);
                    resultMd5.put(appname, registerInstancesMd5);
                }

                // fill local data
                discoveryAppnameStore.putAll(result);
                discoveryAppnameMd5Store.putAll(resultMd5);

                return result;
            }
        } catch (Exception e) {
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
