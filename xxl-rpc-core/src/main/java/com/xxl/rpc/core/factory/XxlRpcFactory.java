package com.xxl.rpc.core.factory;

import com.xxl.rpc.core.factory.config.BaseConfig;
import com.xxl.rpc.core.invoker.InvokerFactory;
import com.xxl.rpc.core.invoker.config.InvokerConfig;
import com.xxl.rpc.core.provider.ProviderFactory;
import com.xxl.rpc.core.provider.config.ProviderConfig;
import com.xxl.rpc.core.register.Register;
import com.xxl.rpc.core.register.config.RegisterConfig;
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
public class XxlRpcFactory {
    private static final Logger logger = LoggerFactory.getLogger(XxlRpcFactory.class);

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
        if (register != null) {
            try {
                register.start(this);       // registry-discovery thread run
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (providerConfig!=null) {
            try {
                provider = new ProviderFactory();
                provider.start(this);       // invoke registry-registry, run thread
            } catch (Exception e) {
                throw new XxlRpcException(e);
            }
        }
        if (invokerConfig!=null) {
            try {
                invoker = new InvokerFactory();
                invoker.start(this);        // invoke registry-discovery(spring), run thread
            } catch (Exception e) {
                throw new XxlRpcException(e);
            }
        }
        logger.info(">>>>>>>>>>> xxl-rpc start success.");
    }

    /**
     * stop
     */
    public void stop(){
        // stop server
        if (register != null) {
            try {
                register.stop();
            } catch (Exception e) {
                throw new XxlRpcException(e);
            }
        }
        if (provider!=null) {
            try {
                provider.stop();
            } catch (Exception e) {
                throw new XxlRpcException(e);
            }
        }
        if (invoker!=null) {
            try {
                invoker.stop();
            } catch (Exception e) {
                throw new XxlRpcException(e);
            }
        }

        // finish stopCallable
        finishStopCallable();

        logger.info(">>>>>>>>>>> xxl-rpc stopped.");
    }

    // ---------------------- start / stop ----------------------

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
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
