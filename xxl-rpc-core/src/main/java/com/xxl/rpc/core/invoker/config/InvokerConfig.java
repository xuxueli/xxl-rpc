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
     * provider switch
     */
    private boolean open = true;

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
    public InvokerConfig(boolean open, Class<? extends Client> client, Class<? extends Serializer> serializer, List<String> serializerAllowPackageList) {
        this.open = open;
        this.client = client;
        this.serializer = serializer;
        this.serializerAllowPackageList = serializerAllowPackageList;
    }
    public InvokerConfig(boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
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
