package com.xxl.rpc.core.provider.config;

import com.xxl.rpc.core.remoting.Server;
import com.xxl.rpc.core.remoting.impl.netty.server.NettyServer;
import com.xxl.rpc.core.serializer.Serializer;
import com.xxl.rpc.core.serializer.impl.JsonbSerializer;

import java.util.List;

/**
 * Provider Config
 *
 * @author xuxueli 2024-1221
 */
public class ProviderConfig {

    /**
     * provider switch
     */
    private boolean enable = true;

    /**
     * server, for network
     */
    private Class<? extends Server> server = NettyServer.class;

    /**
     * serializer, process request and response
     */
    private Class<? extends Serializer> serializer = JsonbSerializer.class;

    /**
     * serializer allow package list, for security
     */
    private List<String> serializerAllowPackageList;

    /**
     * server port (generate address)
     */
    private int port = 7080;

    /**
     * handler thread-pool core size
     */
    private int corePoolSize = 60;

    /**
     * handler thread-pool max size
     */
    private int maxPoolSize = 300;

    /**
     * register address (optional), will use "ip:port" if not exists
     */
    private String address;

    /**
     * accessToken (optional), for rpc-safe
     */
    //private String accessToken;   // TODO-1，静态配置token废弃，借助注册中心实现动态token；

    public ProviderConfig() {
    }
    public ProviderConfig(boolean enable,
                          Class<? extends Server> server,
                          Class<? extends Serializer> serializer,
                          List<String> serializerAllowPackageList,
                          int port,
                          int corePoolSize,
                          int maxPoolSize,
                          String address) {
        this.enable = enable;
        this.server = server;
        this.serializer = serializer;
        this.serializerAllowPackageList = serializerAllowPackageList;
        this.port = port;
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.address = address;
    }

    public ProviderConfig(boolean enable){
        this.enable = enable;
    }

    public Class<? extends Server> getServer() {
        return server;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setServer(Class<? extends Server> server) {
        this.server = server;
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
