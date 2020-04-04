package com.xxl.rpc.core.registry.impl.xxlrpcadmin.model;

import java.util.List;

/**
 * @author xuxueli 2018-12-03
 */
public class XxlRpcAdminRegistryParamVO {


    private String accessToken;
    private String biz;
    private String env;


    private List<XxlRpcAdminRegistryDataParamVO> registryDataList;
    private List<String> keys;

    public String getBiz() {
        return biz;
    }

    public void setBiz(String biz) {
        this.biz = biz;
    }

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

    public List<XxlRpcAdminRegistryDataParamVO> getRegistryDataList() {
        return registryDataList;
    }

    public void setRegistryDataList(List<XxlRpcAdminRegistryDataParamVO> registryDataList) {
        this.registryDataList = registryDataList;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }
}
