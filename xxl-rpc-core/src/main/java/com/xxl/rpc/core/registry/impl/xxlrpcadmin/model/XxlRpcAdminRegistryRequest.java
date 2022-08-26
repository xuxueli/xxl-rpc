package com.xxl.rpc.core.registry.impl.xxlrpcadmin.model;

import java.util.List;

/**
 * @author xuxueli 2018-12-03
 */
public class XxlRpcAdminRegistryRequest {


    private String accessToken;
    private String env;

    private List<XxlRpcAdminRegistryDataItem> registryDataList;
    private List<String> keys;

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public List<XxlRpcAdminRegistryDataItem> getRegistryDataList() {
        return registryDataList;
    }

    public void setRegistryDataList(List<XxlRpcAdminRegistryDataItem> registryDataList) {
        this.registryDataList = registryDataList;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }
}
