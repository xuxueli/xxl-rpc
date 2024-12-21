package com.xxl.rpc.core.factory.config;

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

}
