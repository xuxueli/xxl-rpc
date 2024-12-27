package com.xxl.rpc.core.register.entity;


import java.io.Serializable;
import java.util.Objects;

/**
 *  Register Instance
 *
 *  Created by xuxueli on '2024-12-15 11:08:18'.
 */
public class RegisterInstance implements Serializable, Comparable {
    private static final long serialVersionUID = 42L;

    /**
     * Env
     */
    private String env;

    /**
     * AppName（应用唯一标识）
     */
    private String appname;

    /**
     * 注册节点IP
     */
    private String ip;

    /**
     * 注册节点端口号
     */
    private int port;

    /**
     * 扩展信息
     */
    private String extendInfo;

    public RegisterInstance() {
    }
    public RegisterInstance(String env, String appname, String ip, int port, String extendInfo) {
        this.env = env;
        this.appname = appname;
        this.ip = ip;
        this.port = port;
        this.extendInfo = extendInfo;
    }

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

    public String getExtendInfo() {
        return extendInfo;
    }

    public void setExtendInfo(String extendInfo) {
        this.extendInfo = extendInfo;
    }

    @Override
    public String toString() {
        return "InstanceCacheDTO{" +
                "env='" + env + '\'' +
                ", appname='" + appname + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", extendInfo='" + extendInfo + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        RegisterInstance that = (RegisterInstance) object;
        return port == that.port && Objects.equals(env, that.env) && Objects.equals(appname, that.appname) && Objects.equals(ip, that.ip) && Objects.equals(extendInfo, that.extendInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(env, appname, ip, port, extendInfo);
    }

    // tool

    /**
     * get sort key
     * @return
     */
    public String getSortKey(){
        return ip + ":" + port;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof RegisterInstance) {
            RegisterInstance that = (RegisterInstance) o;
            return this.getSortKey().compareTo(that.getSortKey());
        }
        return 0;
    }

}