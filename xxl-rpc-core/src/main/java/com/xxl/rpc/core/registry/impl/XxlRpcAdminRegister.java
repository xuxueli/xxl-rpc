package com.xxl.rpc.core.registry.impl;

import com.xxl.rpc.core.registry.impl.xxlrpcadmin.XxlRpcAdminRegistryClient;
import com.xxl.rpc.core.registry.impl.xxlrpcadmin.model.XxlRpcAdminRegistryDataParamVO;
import com.xxl.rpc.core.registry.Register;

import java.util.*;

/**
 * application registry for "xxl-rpc-admin"
 *
 * @author xuxueli 2018-11-30
 */
public class XxlRpcAdminRegister extends Register {

    public static final String ADMIN_ADDRESS = "ADMIN_ADDRESS";
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String BIZ = "BIZ";
    public static final String ENV = "ENV";

    private XxlRpcAdminRegistryClient xxlRpcAdminRegistryClient;
    public XxlRpcAdminRegistryClient getXxlRpcAdminRegistryClient() {
        return xxlRpcAdminRegistryClient;
    }

    @Override
    public void start(Map<String, String> param) {
        String xxlRegistryAddress = param.get(ADMIN_ADDRESS);
        String accessToken = param.get(ACCESS_TOKEN);
        String biz = param.get(BIZ);
        String env = param.get(ENV);

        // fill
        biz = (biz!=null&&biz.trim().length()>0)?biz:"default";
        env = (env!=null&&env.trim().length()>0)?env:"default";

        xxlRpcAdminRegistryClient = new XxlRpcAdminRegistryClient(xxlRegistryAddress, accessToken, biz, env);
    }

    @Override
    public void stop() {
        if (xxlRpcAdminRegistryClient != null) {
            xxlRpcAdminRegistryClient.stop();
        }
    }

    @Override
    public boolean registry(Set<String> keys, String value) {
        if (keys==null || keys.size() == 0 || value==null || value.trim().length()==0) {
            return false;
        }

        // init
        List<XxlRpcAdminRegistryDataParamVO> registryDataList = new ArrayList<>();
        for (String key:keys) {
            registryDataList.add(new XxlRpcAdminRegistryDataParamVO(key, value));
        }

        return xxlRpcAdminRegistryClient.registry(registryDataList);
    }

    @Override
    public boolean remove(Set<String> keys, String value) {
        if (keys==null || keys.size() == 0 || value==null || value.trim().length()==0) {
            return false;
        }

        // init
        List<XxlRpcAdminRegistryDataParamVO> registryDataList = new ArrayList<>();
        for (String key:keys) {
            registryDataList.add(new XxlRpcAdminRegistryDataParamVO(key, value));
        }

        return xxlRpcAdminRegistryClient.remove(registryDataList);
    }

    @Override
    public Map<String, TreeSet<String>> discovery(Set<String> appkeys) {
        return xxlRpcAdminRegistryClient.discovery(appkeys);
    }

    @Override
    public TreeSet<String> discovery(String appkey) {
        return xxlRpcAdminRegistryClient.discovery(appkey);
    }

}
