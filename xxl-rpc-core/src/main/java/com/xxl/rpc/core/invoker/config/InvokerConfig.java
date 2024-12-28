package com.xxl.rpc.core.invoker.config;

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
     * accessToken (optional), for rpc-safe
     */
    //private String accessToken;

    // inteceptor

    public InvokerConfig() {
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

}
