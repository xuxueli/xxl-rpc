package com.xxl.rpc.core.boot.config;

import com.xxl.rpc.core.util.XxlRpcException;

public class BaseConfig {

    /**
     * env
     */
    private String env;

    /**
     * appname
     */
    private String appname;

    public BaseConfig() {
    }
    public BaseConfig(String env, String appname) {
        this.env = env;
        this.appname = appname;
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


    // ---------------------- valid ----------------------

    /**
     * valid base config
     */
    public void valid() {
        if (env == null || env.trim().isEmpty()) {
            throw new XxlRpcException("xxl-rpc BaseConfig invalid, env not exists.");
        }
        if (appname == null || appname.trim().isEmpty()) {
            throw new XxlRpcException("xxl-rpc BaseConfig invalid, appname not exists");
        }
    }

}
