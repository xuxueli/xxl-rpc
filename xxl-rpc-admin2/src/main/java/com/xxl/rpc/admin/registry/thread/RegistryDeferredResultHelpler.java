package com.xxl.rpc.admin.registry.thread;


import com.xxl.rpc.admin.registry.model.OpenApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * registry DeferredResult helpler
 *
 * 功能：
 * 1、客户端连接保活功能：以instance 维护关注的 客户端监听器 集合；
 * 2、变更推动通道：接收到注册信息变更后，提供通道能力，通知客户端监听器、实时更新客户端数据；
 *
 * 面向：
 * 1、服务consumer：提供注册变更推送通道
 *
 * @author xuxueli
 */
public class RegistryDeferredResultHelpler {
    private static Logger logger = LoggerFactory.getLogger(RegistryDeferredResultHelpler.class);

    /**
     * 客户端监听器
     *
     * <pre>
     *     说明：以instance 维护关注的 客户端监听器 集合；
     *     Cache-Data：
     *          Key：String （与 RegistryCacheHelpler 缓存key保持一致）
     *              格式：env##appname
     *              示例："test##app02"
     *          Value：List
     *              格式：DeferredResult，客户端注册
     * </pre>
     */
    private volatile Map<String, List<DeferredResult>> registryDeferredResultMap = new ConcurrentHashMap<>();

    /**
     * registry monitor (will remove instance that expired more than 1 day)
     */
    private Thread deferredResultMonitorThread;

    /**
     * thread stop variable
     */
    private volatile boolean toStop = false;


    /**
     * start
     */
    public void start() {

        // 2、deferredResultMonitorThread， for clean
        deferredResultMonitorThread = RegistryCacheHelpler.startThread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {
                        // TODO, clean dead DeferredResult
                        if (!registryDeferredResultMap.isEmpty()) {
                            for (Map.Entry<String, List<DeferredResult>> entry : registryDeferredResultMap.entrySet()) {

                                if (entry.getValue().size() > 0) {
                                    List<DeferredResult> toRemove = new ArrayList<>();
                                    for (DeferredResult deferredResult : entry.getValue()) {
                                        if (deferredResult.isSetOrExpired()) {
                                            toRemove.add(deferredResult);
                                        }
                                    }

                                    entry.getValue().removeAll(toRemove);
                                }

                            }
                        }



                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-rpc, RegistryDeferredResultHelpler-deferredResultMonitorThread error:{}", e.getMessage(), e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(RegistryCacheHelpler.REGISTRY_BEAT_TIME * 3);
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-rpc, RegistryDeferredResultHelpler-deferredResultMonitorThread error2:{}", e.getMessage(), e);
                        }
                    }
                }
                logger.info("xxl-rpc, admin RegistryDeferredResultHelpler-deferredResultMonitorThread finish.");
            }
        }, "xxl-rpc, admin RegistryDeferredResultHelpler-deferredResultMonitorThread");

    }

    public void stop() {

    }

    // ---------------------- helper ----------------------

    /**
     * pushClient
     *
     * @param envAppnameList
     * @return
     */
    public String pushClient(List<String> envAppnameList){

        // fileName
        String fileName = "";

        // valid repeat update

        // brocast monitor client
        List<DeferredResult> deferredResultList = registryDeferredResultMap.get(fileName);
        if (deferredResultList != null) {
            registryDeferredResultMap.remove(fileName);
            for (DeferredResult deferredResult: deferredResultList) {

                deferredResult.setResult(new OpenApiResponse<>(OpenApiResponse.SUCCESS_CODE, "Monitor key update."));
            }
        }

        return new File(fileName).getPath();
    }

    /**
     * monitor
     *
     * @param request
     * @return
     */
    public DeferredResult<Object> monitor(Object request) {

        // init
        DeferredResult deferredResult = new DeferredResult(30 * 1000L, new OpenApiResponse<>(OpenApiResponse.SUCCESS_CODE, "Monitor timeout, no key updated."));

        // valid


        // monitor by client
        // TODO，伪代码
        for (String key: Arrays.asList(request.toString().split(","))) {
            String fileName = "";

            List<DeferredResult> deferredResultList = registryDeferredResultMap.get(fileName);
            if (deferredResultList == null) {
                deferredResultList = new ArrayList<>();
                registryDeferredResultMap.put(fileName, deferredResultList);
            }

            deferredResultList.add(deferredResult);
        }

        return deferredResult;
    }

}
