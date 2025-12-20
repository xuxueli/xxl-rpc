package com.xxl.rpc.core.invoker.config;

import com.xxl.rpc.core.remoting.Client;
import com.xxl.rpc.core.remoting.impl.netty.client.NettyClient;
import com.xxl.rpc.core.serializer.Serializer;
import com.xxl.rpc.core.serializer.impl.JsonbSerializer;

import java.util.List;

/**
 * invoke config
 *
 * @author xuxueli 2024-12-28
 */
public class InvokerConfig {

    /**
     * invoker switch
     */
    private boolean enable = true;

    /**
     * client, for network
     */
    private Class<? extends Client> client = NettyClient.class;

    /**
     * serializer, process request and response
     */
    private Class<? extends Serializer> serializer = JsonbSerializer.class;

    /**
     * serializer allow package list, for security
     */
    private List<String> serializerAllowPackageList;

    /**
     * accessToken (optional), for rpc-safe
     */
    //private String accessToken;

    // inteceptor

    public InvokerConfig() {
    }
    public InvokerConfig(boolean enable, Class<? extends Client> client, Class<? extends Serializer> serializer, List<String> serializerAllowPackageList) {
        this.enable = enable;
        this.client = client;
        this.serializer = serializer;
        this.serializerAllowPackageList = serializerAllowPackageList;
    }
    public InvokerConfig(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Class<? extends Client> getClient() {
        return client;
    }

    public void setClient(Class<? extends Client> client) {
        this.client = client;
    }

    public Class<? extends Serializer> getSerializer() {
        return serializer;
    }

    public void setSerializer(Class<? extends Serializer> serializer) {
        this.serializer = serializer;
    }

    public List<String> getSerializerAllowPackageList() {
        return serializerAllowPackageList;
    }

    public void setSerializerAllowPackageList(List<String> serializerAllowPackageList) {
        this.serializerAllowPackageList = serializerAllowPackageList;
    }

}
