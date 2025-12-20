package com.xxl.rpc.core.boot.support;

import com.xxl.rpc.core.boot.XxlRpcBootstrap;
import com.xxl.rpc.core.invoker.reference.XxlRpcReferenceBean;
import com.xxl.rpc.core.invoker.support.SpringInvokerFactory;
import com.xxl.rpc.core.provider.support.SpringProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;

/**
 * XxlRpc Spring Factory
 *
 * @author xuxueli 2024-12-21
 */
public class SpringXxlRpcBootstrap extends XxlRpcBootstrap implements ApplicationContextAware, SmartInitializingSingleton, DisposableBean, SmartInstantiationAwareBeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(SpringXxlRpcBootstrap.class);

    @Override
    public void afterSingletonsInstantiated() {
        // 1、start
        super.start();

        // 2、discovery referenceBean - instance
        if (getInvokerConfig()!=null && getInvokerConfig().isEnable()) {
            this.getInvoker().addAllReferenceBean(referenceBeanList);
            this.getInvoker().discoveryReferenceBean();
        }

        // 3、provider support：scan service
        if (getProviderConfig()!=null && getProviderConfig().isEnable()) {
            SpringProviderFactory.scanService(applicationContext, this);
        }
    }


    /**
     * referenceBean List
     */
    private volatile List<XxlRpcReferenceBean> referenceBeanList = new ArrayList<>();
    public void addReferenceBean(XxlRpcReferenceBean referenceBean){
        referenceBeanList.add(referenceBean);
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        // invoker open-switch
        if (getInvokerConfig()!=null && getInvokerConfig().isEnable()) {
            /**
             * invoker support
             *
             * 1、init XxlRpcReferenceBean
             * 2、collect XxlRpcReferenceBean
             */
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
