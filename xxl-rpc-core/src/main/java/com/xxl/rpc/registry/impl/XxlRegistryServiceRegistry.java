package com.xxl.rpc.registry.impl;

import com.xxl.registry.client.XxlRegistryClient;
import com.xxl.registry.client.model.XxlRegistryParam;
import com.xxl.rpc.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2018-11-30
 */
public class XxlRegistryServiceRegistry extends ServiceRegistry {
    private static Logger logger = LoggerFactory.getLogger(XxlRegistryServiceRegistry.class);


    public static final String XXL_REGISTRY_ADDRESS = "XXL_REGISTRY_ADDRESS";
    public static final String ENV = "ENV";


    private XxlRegistryClient registryClient;


    private volatile Set<XxlRegistryParam> registryData = new HashSet<>();
    private volatile ConcurrentMap<String, TreeSet<String>> discoveryData = new ConcurrentHashMap<String, TreeSet<String>>();


    private Thread registryThread;
    private Thread discoveryThread;
    private volatile boolean registryThreadStop = false;


    @Override
    public void start(Map<String, String> param) {
        String xxlRegistryAddress = param.get(XXL_REGISTRY_ADDRESS);
        String env = param.get(ENV);

        registryClient = new XxlRegistryClient(xxlRegistryAddress, "xxl-rpc", env);
        logger.info(">>>>>>>>>>> xxl-rpc, XxlRegistryServiceRegistry init .... [xxlRegistryAddress={}, env={}]", xxlRegistryAddress, env);

        // registry thread
        registryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!registryThreadStop) {
                    try {
                        if (registryData.size() > 0) {

                            registryClient.registry(new ArrayList<XxlRegistryParam>(registryData));
                            logger.info(">>>>>>>>>>> xxl-rpc, refresh registry data success, registryData = {}", registryData);
                        }
                    } catch (Exception e) {
                        if (!registryThreadStop) {
                            logger.error(">>>>>>>>>>> xxl-rpc, refresh thread error.", e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (Exception e) {
                        if (!registryThreadStop) {
                            logger.error(">>>>>>>>>>> xxl-rpc, refresh thread error.", e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> xxl-rpc, refresh thread stoped.");
            }
        });
        registryThread.setName("xxl-rpc, XxlRegistryServiceRegistry refresh thread.");
        registryThread.setDaemon(true);
        registryThread.start();

        // discovery thread
        discoveryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!registryThreadStop) {
                    try {
                        // long polling, monitor, timeout 30s
                        if (discoveryData.size() > 0) {

                            registryClient.monitor(discoveryData.keySet());

                            // refreshDiscoveryData, all
                            refreshDiscoveryData(discoveryData.keySet());
                        }
                    } catch (Exception e) {
                        if (!registryThreadStop) {
                            logger.error(">>>>>>>>>>> xxl-rpc, refresh thread error.", e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (Exception e) {
                        if (!registryThreadStop) {
                            logger.error(">>>>>>>>>>> xxl-rpc, refresh thread error.", e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> xxl-rpc, refresh thread stoped.");
            }
        });
        discoveryThread.setName("xxl-rpc, XxlRegistryServiceRegistry refresh thread.");
        discoveryThread.setDaemon(true);
        discoveryThread.start();

        logger.info(">>>>>>>>>>> xxl-rpc, XxlRegistryServiceRegistry init success.");
    }

    @Override
    public void stop() {
        registryThreadStop = true;
        if (registryThread != null) {
            registryThread.interrupt();
        }
        if (discoveryThread != null) {
            discoveryThread.interrupt();
        }
    }

    @Override
    public boolean registry(Set<String> keys, String value) {
        if (keys==null || keys.size() == 0 || value == null) {
            return false;
        }

        // init
        List<XxlRegistryParam> registryParamList = new ArrayList<>();
        for (String key:keys) {
            registryParamList.add(new XxlRegistryParam(key, value));
        }

        // cache
        registryData.addAll(registryParamList);

        // remote
        registryClient.registry(registryParamList);

        return true;
    }

    @Override
    public boolean remove(Set<String> keys, String value) {
        if (keys==null || keys.size() == 0 || value == null) {
            return false;
        }

        // init
        List<XxlRegistryParam> registryParamList = new ArrayList<>();
        for (String key:keys) {
            registryParamList.add(new XxlRegistryParam(key, value));
        }

        // cache
        registryData.removeAll(registryParamList);


        // remote
        registryClient.remove(registryParamList);

        return true;
    }

    @Override
    public Map<String, TreeSet<String>> discovery(Set<String> keys) {
        if (keys==null || keys.size() == 0) {
            return null;
        }

        // find from local
        Map<String, TreeSet<String>> registryDataTmp = new HashMap<String, TreeSet<String>>();
        for (String key : keys) {
            TreeSet<String> valueSet = discoveryData.get(key);
            if (valueSet != null) {
                registryDataTmp.put(key, valueSet);
            }
        }

        // not find all, find from remote
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

    /**
     * refreshDiscoveryData, some or all
     */
    private void refreshDiscoveryData(Set<String> keys){
        if (keys==null || keys.size() == 0) {
            return;
        }

        // discovery mult
        Map<String, TreeSet<String>> keyValueListData = registryClient.discovery(keys);
        if (keyValueListData!=null) {
            for (String keyItem: keyValueListData.keySet()) {
                discoveryData.put(keyItem, keyValueListData.get(keyItem));
            }
        }
        logger.info(">>>>>>>>>>> xxl-rpc, refresh discovery data success, discoveryData = {}", discoveryData);
    }

    @Override
    public TreeSet<String> discovery(String key) {
        Map<String, TreeSet<String>> keyValueSetTmp = discovery(new HashSet<String>(Arrays.asList(key)));
        if (keyValueSetTmp != null) {
            return keyValueSetTmp.get(key);
        }
        return null;
    }


}
