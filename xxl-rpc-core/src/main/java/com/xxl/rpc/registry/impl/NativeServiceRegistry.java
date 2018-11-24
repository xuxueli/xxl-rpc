package com.xxl.rpc.registry.impl;

import com.xxl.rpc.registry.ServiceRegistry;
import com.xxl.rpc.util.NaticveClient;
import com.xxl.rpc.util.XxlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2018-11-24 22:48:57
 */
public class NativeServiceRegistry extends ServiceRegistry {
    private static Logger logger = LoggerFactory.getLogger(ZkServiceRegistry.class);

    public static final String XXL_RPC_ADMIN = "env";                       // native registry env
    public static final String ENV = "env";                       // native registry env


    // param
    private String adminAddress = null;
    private String biz = "xxl-rpc";
    private String env;

    private List<String> adminAddressArr = null;


    private volatile ConcurrentMap<String, TreeSet<String>> registryData = new ConcurrentHashMap<String, TreeSet<String>>();
    private volatile ConcurrentMap<String, TreeSet<String>> discoveryData = new ConcurrentHashMap<String, TreeSet<String>>();
    private Thread refreshThread;
    private volatile boolean refreshThreadStop = false;


    @Override
    public void start(Map<String, String> param) {
        this.adminAddress = param.get(XXL_RPC_ADMIN);
        this.env = param.get(ENV);

        // valid
        if (adminAddress==null || adminAddress.trim().length()==0) {
            throw new XxlRpcException("xxl-rpc adminAddress can not be empty");
        }
        // admin address
        adminAddressArr = new ArrayList<>();
        if (adminAddress.contains(",")) {
            adminAddressArr.add(adminAddress);
        } else {
            adminAddressArr.addAll(Arrays.asList(adminAddress.split(",")));
        }

        // init zkpath
        if (env==null || env.trim().length()==0) {
            throw new XxlRpcException("xxl-rpc env can not be empty");
        }

        // registry thread
        refreshThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!refreshThreadStop) {
                    try {

                        // long polling, monitor, timeout 10s
                        // TODO, discovery , monitor


                        // refreshDiscoveryData, all
                        refreshDiscoveryData(null);
                    } catch (Exception e) {
                        if (!refreshThreadStop) {
                            logger.error(">>>>>>>>>> xxl-rpc, refresh thread error.", e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (Exception e) {
                        if (!refreshThreadStop) {
                            logger.error(">>>>>>>>>> xxl-rpc, refresh thread error.", e);
                        }
                    }
                }
                logger.info(">>>>>>>>>> xxl-rpc, refresh thread stoped.");
            }
        });
        refreshThread.setName("xxl-rpc, NativeServiceRegistry refresh thread.");
        refreshThread.setDaemon(true);
        refreshThread.start();

        logger.info(">>>>>>>>>> xxl-rpc, NativeServiceRegistry init success. [adminAddress=[], env={}]", adminAddress, env);
    }

    @Override
    public void stop() {
        if (refreshThread != null) {
            refreshThreadStop = true;
            refreshThread.interrupt();
        }
    }

    /**
     * refreshDiscoveryData, some or all
     */
    private void refreshDiscoveryData(Set<String> keys){
        if (keys==null && discoveryData.size() > 0) {
            keys = new HashSet<String>();
            keys.addAll(discoveryData.keySet());
        }

        if (keys.size() > 0) {
            // discovery mult
            Map<String, List<String>> keyValueListData = NaticveClient.discovery(adminAddressArr, biz, env, keys);
                if (keyValueListData!=null) {
                for (String keyItem: keyValueListData.keySet()) {
                    discoveryData.put(keyItem, new TreeSet<String>(keyValueListData.get(keyItem)));
                }
            }
        }

    }

    @Override
    public boolean registry(Set<String> keys, String value) {
        // local cache
        for (String key : keys) {

            TreeSet<String> values = registryData.get(key);
            if (values == null) {
                values = new TreeSet<>();
                registryData.put(key, values);
            }
            values.add(value);
        }

        // remove mult
        boolean ret = NaticveClient.registry(adminAddressArr, biz, env, keys, value);

        return ret;
    }

    @Override
    public boolean remove(Set<String> keys, String value) {
        for (String key : keys) {
            TreeSet<String> values = discoveryData.get(key);
            if (values != null) {
                values.remove(value);
            }
        }

        // remove mult
        boolean ret = NaticveClient.remove(adminAddressArr, biz, env, keys, value);

        return ret;
    }

    @Override
    public Map<String, TreeSet<String>> discovery(Set<String> keys) {

        // find from local
        Map<String, TreeSet<String>> registryDataTmp = new HashMap<String, TreeSet<String>>();
        for (String key : keys) {
            TreeSet<String> valueSet = discoveryData.get(key);
            if (valueSet != null) {
                registryDataTmp.put(key, valueSet);
            }
        }

        // fail, find from remote
        if (keys.size() != registryDataTmp.size()) {

            // refreshDiscoveryData, some, first use
            refreshDiscoveryData(keys);

            // find from local
            for (String key : keys) {
                TreeSet<String> valueSet = discoveryData.get(key);
                if (valueSet != null) {
                    registryDataTmp.put(key, valueSet);
                }
            }
        }

        return registryDataTmp;
    }

    @Override
    public TreeSet<String> discovery(String key) {
        Map<String, TreeSet<String>> keyValueSetTmp = discovery(new HashSet<String>(Arrays.asList(key)));
        if (keyValueSetTmp!=null) {
            return keyValueSetTmp.get(key);
        }
        return null;
    }

}
