package com.xxl.rpc.admin.registry.thread;

import com.xxl.rpc.admin.model.entity.Instance;
import com.xxl.rpc.admin.registry.model.OpenApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import static com.xxl.rpc.admin.registry.model.OpenApiResponse.SUCCESS_CODE;

/**
 * registry cache helper
 *
 * 功能：
 * 1、注册信息本地（伪分布式）缓存能力：全（全量缓存）、准（全量更新 + 增量更新）、实时（秒级广播）；
 * 2、缓存数据变更、主动推动客户端能力：“全量 + 增量 + 实时” 检测不一致时，各节点匹配监听的client，并主动推送；（触发 RegistryDeferredResultHelpler 周知客户端）
 *
 * 面向：
 * 1、服务consumer：提供注册信息查询能力、注册变更推送能力；
 *
 * @author xuxueli
 */
public class RegistryCacheHelpler {
    private static Logger logger = LoggerFactory.getLogger(RegistryCacheHelpler.class);

    /**
     * 注册信息本地缓存：完整数据
     *
     * <pre>
     *      DB-Instance：DB完整注册信息。
     *          env：Env（环境唯一标识）
     *          appname：AppName（应用唯一标识）
     *          ip：注册节点IP
     *          port：注册节点端口号
     *          registerModel：注册模式
     *          registerHeartbeat：节点最后心跳时间，动态注册时判定是否过期
     *          extendInfo：扩展信息
     *      Cache-Data：
     *          Key：String
     *              格式：env##appname
     *              示例："test##app02"
     *          Value：ArrayList<Object>
     *              ip
     *              port
     *              extendInfo
     *      注册数据是否有效，判定逻辑：
     *          动态注册：心跳间隔30s，三倍间隔时间内存在心跳判定有效，否则无效；(registerModel + registerHeartbeat)
     *          持久化注册：永久有效；
     *          禁用注册：无效
     * </pre>
     */
    private volatile ConcurrentMap<String, List<Instance>> registryCacheStore = new ConcurrentHashMap<>();
    /**
     * 注册信息本地缓存：MD5摘要
     *
     * <pre>
     *     说明：数据为注册信息JSON序列化后再Md5的信息，用户快速比对缓存与客户端信息是否一致。一致则不进行更新，幂等跳过；否则再详细比对。
     *     Cache-Data：
     *          Key：String
     *              格式：env##appname
     *              示例："test##app02"
     *          Value：String
     *              格式：md5( registryCacheStore#value )
     * </pre>
     */
    private volatile ConcurrentMap<String, String> registryCacheMd5Store = new ConcurrentHashMap<>();

    /**
     * BeatTime Interval, by second
     */
    public static final int REGISTRY_BEAT_TIME = 30;

    /**
     * thread stop variable
     */
    private volatile boolean toStop = false;

    /**
     * first full-sync status, true if sync success
     */
    private volatile boolean warmUp = false;

    /**
     * message filtering to avoid duplicate processing
     */
    private volatile List<Integer> readedMessageIds = Collections.synchronizedList(new ArrayList<Integer>());

    /**
     * 全量同步
     * 1、范围：DB中全量注册数据，同步至 registryCacheStore
     * 2、间隔：10倍心跳（REGISTRY_BEAT_TIME * 10）；
     * 3、过滤：过滤掉无效数据；
     */
    private Thread fullSyncThread;

    /**
     * 增量同步
     * 1、说明：DB中部分注册数据，UpdateTime在 三倍心跳时间内（REGISTRY_BEAT_TIME * 3），同步至 registryCacheStore
     * 2、间隔：1倍心跳（REGISTRY_BEAT_TIME * 1）；
     * 3、过滤：过滤掉无效数据；
     */
    private Thread incrSyncThread;

    /**
     * 实时同步
     * 1、说明：实时监听广播消息，根据消息类型实时更新指定注册数据，从DB 同步至 registryCacheStore
     * 2、间隔：1s/次，实时检测广播消息；无消息则跳过；
     * 3、过滤：过滤掉无效数据；
     */
    private Thread messageListenThread;

    /**
     * start
     */
    public void start(){
        // 1、run fullSyncThread
        fullSyncThread = startThread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {
                        // 1、TODO,DB中全量注册数据，同步至 registryCacheStore,
                        // TODO 过滤掉无效数据

                        // TODO，发现不一致数据，客户端推送
                        pushClient();


                        // first full-sycs success, warmUp
                        if (!warmUp) {
                            warmUp = true;
                            logger.info(">>>>>>>>>>> xxl-rpc, admin RegistryCacheHelpler-fullSyncThread warmUp finish");
                        }
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-rpc, admin RegistryCacheHelpler-fullSyncThread error:{}", e.getMessage(), e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(REGISTRY_BEAT_TIME * 10);
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-rpc, admin RegistryCacheHelpler-fullSyncThread error2:{}", e.getMessage(), e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> xxl-rpc, admin RegistryCacheHelpler-fullSyncThread stop");
            }
        }, "xxl-rpc, admin RegistryCacheHelpler-fullSyncThread");

        // 2、incrSyncThread
        incrSyncThread = startThread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {
                        // 2、TODO DB中部分注册数据，UpdateTime在 三倍心跳时间内（REGISTRY_BEAT_TIME * 3），同步至 registryCacheStore
                        // TODO 过滤掉无效数据

                        // TODO，发现不一致数据，客户端推送
                        pushClient();

                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-rpc, admin RegistryCacheHelpler-fullSyncThread error:{}", e.getMessage(), e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(REGISTRY_BEAT_TIME);
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-rpc, admin RegistryCacheHelpler-fullSyncThread error2:{}", e.getMessage(), e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> xxl-rpc, admin RegistryCacheHelpler-fullSyncThread stop");
            }
        }, "xxl-rpc, admin RegistryCacheHelpler-incrSyncThread");

        // 3、messageListenThread
        messageListenThread = startThread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {

                        // TODO，实时监听广播消息，根据消息类型实时更新指定注册数据，从DB 同步至 registryCacheStore
                        // 2、间隔：1s/次，实时检测广播消息；无消息则跳过；

                        // TODO，消息定期清理，DB + 本次消费记录
                        // clean old message
                        if ( (System.currentTimeMillis()/1000) % REGISTRY_BEAT_TIME ==0) {
                            // xxlRpcRegistryMessageDao.cleanMessage(registryBeatTime);
                            readedMessageIds.clear();
                        }

                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-rpc, admin RegistryCacheHelpler-fullSyncThread error:{}", e.getMessage(), e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(1);
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
     * find changed-data, push client
     */
    private void pushClient(){
        // TODO，发现不一致数据，客户端推送
    }

    /**
     * stop
     */
    public void stop(){
        // mark stop
        toStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }

        // stop thread
        stopThread(fullSyncThread);
        stopThread(incrSyncThread);
        stopThread(messageListenThread);

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

    /**
     * make cache key
     *
     * @param env
     * @param appname
     * @return
     */
    private String makeCacheKey(String env, String appname){
        return env.concat("@").concat(appname);
    }

    /**
     * filter valid instance
     * @return
     */
    private boolean filterValidInstance(){
        // 4、TODO，过滤有效注册实例
        return false;
    }


    // ---------------------- helper ----------------------

    /**
     * get instance list
     *
     * @param env
     * @param appname
     * @return
     */
    public OpenApiResponse<List<Instance>> getInstanceList(String env, String appname){

        // TODO，未预热，无法查询
        if (!warmUp) {
            // 返回无法提供服务
        }

        // 5、TODO，缓存查询逻辑
        String key = makeCacheKey(env, appname);
        // registryCacheStore.get(key)
        return new OpenApiResponse<List<Instance>>(SUCCESS_CODE, null);
    }

}
