package com.xxl.rpc.core.boot.support;

import com.xxl.rpc.core.boot.XxlRpcBootstrap;
import com.xxl.rpc.core.invoker.support.SpringInvokerFactory;
import com.xxl.rpc.core.provider.support.SpringProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * XxlRpc Spring Factory
 *
 * @author xuxueli 2024-12-21
 */
public class XxlRpcSpringFactory extends XxlRpcBootstrap implements ApplicationContextAware, SmartInitializingSingleton, DisposableBean, InstantiationAwareBeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(XxlRpcSpringFactory.class);

    @Override
    public void afterSingletonsInstantiated() {
        super.start();

        // provider open-switch
        if (getProviderConfig()!=null && getProviderConfig().isOpen()) {
            // provider support, scan service
            SpringProviderFactory.scanService(applicationContext, this);
        }
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {

        // invoker open-switch
        if (getInvokerConfig()!=null && getInvokerConfig().isOpen()) {
            // invoker support, init-ReferenceBean and discovery-instance
            return SpringInvokerFactory.postProcessAfterInstantiation(bean, beanName, this);
        }
        return true;
    }


    @Override
    public void destroy() throws Exception {
        super.stop();
    }

    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
