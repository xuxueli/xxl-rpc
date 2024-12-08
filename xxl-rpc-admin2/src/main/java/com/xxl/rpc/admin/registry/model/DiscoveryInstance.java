package com.xxl.rpc.admin.registry.model;

import java.io.Serializable;

/**
 * @author xuxueli 2018-12-03
 */
public class DiscoveryInstance implements Serializable {
    public static final long serialVersionUID = 42L;

    /**
     * AppName（应用唯一标识）
     */
    private String appname;

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

}
