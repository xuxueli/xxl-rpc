package com.xxl.rpc.core.boot;

import com.xxl.rpc.core.boot.config.BaseConfig;
import com.xxl.rpc.core.invoker.InvokerFactory;
import com.xxl.rpc.core.invoker.config.InvokerConfig;
import com.xxl.rpc.core.provider.ProviderFactory;
import com.xxl.rpc.core.provider.config.ProviderConfig;
import com.xxl.rpc.core.register.Register;
import com.xxl.rpc.core.util.XxlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * provider、invoker and register
 *
 * @author xuxueli 2015-10-31 22:54:27
 */
public class XxlRpcBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(XxlRpcBootstrap.class);

    // ---------------------- config-base ----------------------

    /**
     * base Config
     */
    private BaseConfig baseConfig;

    /**
     * provider Config
     */
    private ProviderConfig providerConfig;

    /**
     * invoker Config
     */
    private InvokerConfig invokerConfig;

    // set get
    public BaseConfig getBaseConfig() {
        return baseConfig;
    }

    public void setBaseConfig(BaseConfig baseConfig) {
        this.baseConfig = baseConfig;
    }

    public ProviderConfig getProviderConfig() {
        return providerConfig;
    }

    public void setProviderConfig(ProviderConfig providerConfig) {
        this.providerConfig = providerConfig;
    }

    public InvokerConfig getInvokerConfig() {
        return invokerConfig;
    }

    public void setInvokerConfig(InvokerConfig invokerConfig) {
        this.invokerConfig = invokerConfig;
    }


    // ---------------------- core module ----------------------

    /**
     * register
     */
    private volatile Register register;

    /**
     * provider
     */
    private ProviderFactory provider;

    /**
     * invoker
     */
    private InvokerFactory invoker;

    // set get
    public void setRegister(Register register) {
        this.register = register;
    }

    public Register getRegister() {
        return register;
    }

    public ProviderFactory getProvider() {
        return provider;
    }

    public InvokerFactory getInvoker() {
        return invoker;
    }
    public void setProvider(ProviderFactory provider) {
        this.provider = provider;
    }

    public void setInvoker(InvokerFactory invoker) {
        this.invoker = invoker;
    }


    // ---------------------- start / stop ----------------------

    /**
     * start
     */
    public void start(){

        // 0、valid
        if (baseConfig == null) {
            throw new XxlRpcException("xxl-rpc BaseConfig not exists.");
        }
        baseConfig.valid();

        // 1、register start, thread run
        if (register != null) {
            try {
                register.start(this);
            } catch (Exception e) {
                throw new XxlRpcException(e);
            }
        }

        // 2、provider start, remoting-server run
        if (providerConfig!=null && providerConfig.isOpen()) {
            try {
                provider = new ProviderFactory(this);
                provider.start();
            } catch (Exception e) {
                throw new XxlRpcException(e);
            }
        }

        // 3、invoker start
        if (invokerConfig!=null && invokerConfig.isOpen()) {
            try {
                invoker = new InvokerFactory(this);
                invoker.start();
            } catch (Exception e) {
                throw new XxlRpcException(e);
            }
        }

        logger.info(">>>>>>>>>>> xxl-rpc, XxlRpcBootstrap start success.");
    }

    /**
     * stop
     */
    public void stop(){
        // stop server
        if (register != null) {
            try {
                register.stop();
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (provider!=null) {
            try {
                provider.stop();
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (invoker!=null) {
            try {
                invoker.stop();
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }

        // finish stopCallable
        finishStopCallable();

        logger.info(">>>>>>>>>>> xxl-rpc, XxlRpcBootstrap stopped.");
    }


    // ---------------------- Global, shutdown callback ----------------------

    private volatile List<Callable<Void>> stopCallableList = new ArrayList<>();

    /**
     * add stop-callback
     *
     * @param callable
     */
    public void addStopCallable(Callable<Void> callable){
        stopCallableList.add(callable);
    }

    /**
     * invoke when stop
     */
    public void finishStopCallable(){
        for (Callable<Void> callable: stopCallableList) {
            try {
                callable.call();
            } catch (Throwable e) {
                logger.error(">>>>>>>>>>> xxl-rpc, XxlRpcBootstrap finishStopCallable: {}", e.getMessage(), e);
            }
        }
    }

}
