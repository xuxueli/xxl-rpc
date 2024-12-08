package com.xxl.rpc.admin.registry.thread;

import com.xxl.rpc.admin.registry.model.OpenApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.*;

/**
 * Register Helper
 *
 * 功能：
 * 1、服务注册/注销能力：借助线程队列异步处理，批量写入注册 / 注销数据；
 * 2、注册变更，全局广播广播触发：写入后检测“DB与缓存”是否一致，若不一致发送 “注册广播Message”（触发 RegistryCacheHelpler 实时更新缓存）
 * 3、注册信息维护：主动清理长期过期注册信息（超过24H）；
 *
 * 面向：
 * 1、服务provider：提供 注册/注销 能力
 *
 * @author xuxueli
 */
public class RegisterHelper {
    private static Logger logger = LoggerFactory.getLogger(RegisterHelper.class);

    /**
     * Expired To Clean Interval, by hour
     */
    private static final int EXPIRED_TO_CLEAN_TIME = 24;

    /**
     * register or unregister
     */
    private ThreadPoolExecutor registerOrUnregisterThreadPool = null;

    /**
     * registry monitor (will remove instance that expired more than 1 day)
     */
    private Thread registryMonitorThread;

    /**
     * thread stop variable
     */
    private volatile boolean toStop = false;


    /**
     * start
     */
    public void start() {

        // 1、registerOrUnregisterThreadPool， for registry or unregister
        registerOrUnregisterThreadPool = new ThreadPoolExecutor(
                2,
                20,
                30L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(2000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "xxl-rpc, admin RegisterHelper-registerOrUnregisterThreadPool-" + r.hashCode());
                    }
                },
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        r.run();
                        logger.warn(">>>>>>>>>>> xxl-rpc, admin RegisterHelper-registerOrUnregisterThreadPool, registry or unregister too fast, match threadpool rejected handler.");
                    }
                });

        // 2、registryMonitorThread， for registry clean
        registryMonitorThread = RegistryCacheHelpler.startThread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {
                        // TODO, clean dead instance

                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-rpc, admin RegistryCacheHelpler-fullSyncThread error:{}", e.getMessage(), e);
                        }
                    }
                    try {
                        //
                        TimeUnit.HOURS.sleep(EXPIRED_TO_CLEAN_TIME);
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-rpc, admin RegistryCacheHelpler-fullSyncThread error2:{}", e.getMessage(), e);
                        }
                    }
                }
                logger.info("xxl-rpc, admin RegistryCacheHelpler-fullSyncThread");
            }
        }, "xxl-rpc, admin RegistryCacheHelpler-messageListenThread");

    }

    /**
     * stop
     */
    public void stop() {
        // mark stop
        toStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }

        // 1、stop registryOrRemoveThreadPool
        try {
            registerOrUnregisterThreadPool.shutdownNow();
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }

        // 2、registryMonitorThread
        RegistryCacheHelpler.stopThread(registryMonitorThread);


    }

    // ---------------------- helper ----------------------

    /**
     * registry
     *
     * @param object
     * @return
     */
    public OpenApiResponse<String> registry(Object object) {
        // valid
        // TODO

        // async execute
        registerOrUnregisterThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                // TODO, registry logic

            }
        });

        return null;
    }

    /**
     * unregister
     *
     * @param object
     * @return
     */
    public OpenApiResponse<String> unregister(Object object) {
        // valid
        // TODO

        // async execute
        registerOrUnregisterThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                // TODO, registry logic

            }
        });

        return null;
    }

}
