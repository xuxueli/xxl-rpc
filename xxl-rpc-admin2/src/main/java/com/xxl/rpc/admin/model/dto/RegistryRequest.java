package com.xxl.rpc.admin.model.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author xuxueli 2018-12-03
 */
public class RegistryRequest implements Serializable {
    public static final long serialVersionUID = 42L;


    /**
     * Env（环境唯一标识）
     */
    private String env;

    /**
     * AppName（应用唯一标识）
     */
    private String appname;

    /**
     * 注册分组
     */
    private String group;

    /**
     * 注册节点IP
     */
    private String ip;

    /**
     * 注册节点端口号
     */
    private int port;


    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
