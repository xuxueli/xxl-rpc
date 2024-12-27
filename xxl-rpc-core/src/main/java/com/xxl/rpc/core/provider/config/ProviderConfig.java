package com.xxl.rpc.core.provider.config;

import com.xxl.rpc.core.remoting.Server;
import com.xxl.rpc.core.remoting.impl.netty.server.NettyServer;
import com.xxl.rpc.core.serializer.Serializer;
import com.xxl.rpc.core.serializer.impl.JsonbSerializer;

/**
 * Provider Config
 *
 * @author xuxueli 2024-1221
 */
public class ProviderConfig {

    /**
     * server, for network
     */
    private Class<? extends Server> server = NettyServer.class;

    /**
     * serializer, process request and response
     */
    private Class<? extends Serializer> serializer = JsonbSerializer.class;

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
     * accessToken (optional), for rpc-safe
     */
    //private String accessToken;   // TODO-2，静态配置token废弃，借助注册中心实现动态token；

    /**
     * register address (optional), will use "ip:port" if not exists
     */
    private String address;

    public ProviderConfig() {
    }
    public ProviderConfig(Class<? extends Server> server, Class<? extends Serializer> serializer, int port, int corePoolSize, int maxPoolSize, String address) {
        this.server = server;
        this.serializer = serializer;
        this.port = port;
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.address = address;
    }

    public Class<? extends Server> getServer() {
        return server;
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
