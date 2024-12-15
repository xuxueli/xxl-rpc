package com.xxl.rpc.admin.registry.thread;

import com.xxl.rpc.admin.constant.enums.InstanceRegisterModelEnum;
import com.xxl.rpc.admin.model.dto.InstanceCacheDTO;
import com.xxl.rpc.admin.model.entity.Instance;
import com.xxl.rpc.admin.registry.config.XxlRpcAdminRegistry;
import com.xxl.rpc.admin.registry.model.OpenApiResponse;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.DateTool;
import com.xxl.tool.encrypt.Md5Tool;
import com.xxl.tool.gson.GsonTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.xxl.rpc.admin.registry.model.OpenApiResponse.SUCCESS_CODE;

/**
 * registry cache helper
 *
 * 功能：
 * 1、注册信息本地（伪分布式）缓存能力：全量缓存 + 增量更新(实时/秒级广播）；
 * 2、缓存数据变更、主动推动客户端能力：“全量 + 增量/实时” 检测不一致时，各节点匹配监听的client，并主动推送；（触发 RegistryDeferredResultHelpler 周知客户端）
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
    private volatile ConcurrentMap<String, List<InstanceCacheDTO>> registryCacheStore = new ConcurrentHashMap<>();
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
     * 1、范围：DB中全量注册数据，同步至 registryCacheStore；整个Map覆盖更新；
     * 2、间隔：3倍心跳（REGISTRY_BEAT_TIME * 3）；
     * 3、过滤：过滤掉无效数据；
     */
    private Thread fullSyncThread;

    /**
     * 增量(实时)同步
     * 1、说明：实时监听广播消息，根据消息类型实时更新指定注册数据，从DB 同步至 registryCacheStore；单条数据维度覆盖更新；
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
                // DB中全量注册数据，同步至 registryCacheStore；整个Map覆盖更新；
                while (!toStop) {
                    try {
                        // a、init new map
                        ConcurrentMap<String, List<InstanceCacheDTO>> registryCacheStoreNew = new ConcurrentHashMap<>();
                        ConcurrentMap<String, String> registryCacheMd5StoreNew = new ConcurrentHashMap<>();

                        // b、load all env-appname
                        List<Instance> envAndAppNameList = XxlRpcAdminRegistry.getInstance().getInstanceMapper().queryEnvAndAppName();
                        logger.info(">>>>>>>>>>> xxl-rpc admin, RegistryCacheHelpler - fullSyncThread start, envAndAppNameList:{}", envAndAppNameList);
                        Date registerHeartbeatValid = DateTool.addSeconds(new Date(), -1 * REGISTRY_BEAT_TIME * 3);

                        if (CollectionTool.isNotEmpty(envAndAppNameList)) {
                            // c、process each env-appname
                            for (Instance instance : envAndAppNameList) {
                                // make key
                                String envAppNameKey = instance.getEnv() + "##" + instance.getAppname();
                                List<InstanceCacheDTO> cacheValue = new ArrayList<>();

                                // load value
                                List<Instance> instanceCacheDTOList = XxlRpcAdminRegistry.getInstance().getInstanceMapper().queryByEnvAndAppNameValid(
                                        instance.getEnv(),
                                        instance.getAppname(),
                                        InstanceRegisterModelEnum.AUTO.getValue(),
                                        InstanceRegisterModelEnum.PERSISTENT.getValue(),
                                        registerHeartbeatValid);
                                if (CollectionTool.isNotEmpty(instanceCacheDTOList)){
                                    // convert to cache-dto, and sort by "ip:port"
                                    cacheValue = instanceCacheDTOList
                                            .stream()
                                            .map(InstanceCacheDTO::new)
                                            .sorted(Comparator.comparing(InstanceCacheDTO::getSortKey))     // sort， for md5 match
                                            .collect(Collectors.toList());
                                }

                                // set data
                                registryCacheStoreNew.put(envAppNameKey, cacheValue);
                                registryCacheMd5StoreNew.put(envAppNameKey, Md5Tool.md5(GsonTool.toJson(cacheValue)));      // only match md5, speed up match process
                            }
                        }

                        /**
                         * d、Diff识别不一致数据，客户端推送
                         *
                         * Diff判定逻辑：以旧CacheMap为基础遍历；新Key不存在，不一致；新Key存在但Value不同，不一致；
                         */
                        List<String> envAppnameList = registryCacheMd5Store.keySet().stream()
                                .filter(item -> !registryCacheMd5StoreNew.containsKey(item) || !registryCacheMd5StoreNew.get(item).equals(registryCacheMd5Store.get(item)))
                                .collect(Collectors.toList());
                        pushClient(envAppnameList);

                        // e、replace with new data
                        registryCacheStore = registryCacheStoreNew;
                        registryCacheMd5Store = registryCacheMd5StoreNew;
                        logger.info(">>>>>>>>>>> xxl-rpc admin, RegistryCacheHelpler - fullSyncThread finish, registryCacheStore:{}, registryCacheMd5Store",
                                registryCacheStore, registryCacheMd5Store);

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
                        TimeUnit.SECONDS.sleep(REGISTRY_BEAT_TIME * 3);
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(">>>>>>>>>>> xxl-rpc, admin RegistryCacheHelpler-fullSyncThread error2:{}", e.getMessage(), e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> xxl-rpc, admin RegistryCacheHelpler-fullSyncThread stop");
            }
        }, "xxl-rpc, admin RegistryCacheHelpler-fullSyncThread");

        // 3、messageListenThread
        messageListenThread = startThread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {

                        // TODO，实时监听广播消息，根据消息类型实时更新指定注册数据，从DB 同步至 registryCacheStore
                        // a、间隔：1s/次，实时检测广播消息；无消息则跳过；
                        // XxlRpcAdminRegistry.getInstance().getMessageMapper().queryMessage(readedMessageIds);   // 已读消息入参，幂等过滤


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
    private void pushClient(List<String> envAppnameList){
        if (CollectionTool.isEmpty(envAppnameList)) {
            return;
        }

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
