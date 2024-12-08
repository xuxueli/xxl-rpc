package com.xxl.rpc.admin.registry.config;

import com.xxl.rpc.admin.registry.thread.RegisterHelper;
import com.xxl.rpc.admin.registry.thread.RegistryCacheHelpler;
import com.xxl.rpc.admin.registry.thread.RegistryDeferredResultHelpler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * registry config
 *
 * @author xuxueli
 */
@Configuration
public class XxlRpcAdminRegistry implements InitializingBean, DisposableBean {
    private static Logger logger = LoggerFactory.getLogger(XxlRpcAdminRegistry.class);

    /**
     * 1、RegistryCacheHelpler
     */
    private RegistryCacheHelpler registryCacheHelpler;

    /**
     * 2、RegisterHelper
     */
    private RegisterHelper registerHelper;

    /**
     * 3、RegistryDeferredResultHelpler
     */
    private RegistryDeferredResultHelpler registryDeferredResultHelpler;


    public RegistryCacheHelpler getRegistryCacheHelpler() {
        return registryCacheHelpler;
    }

    public RegisterHelper getRegisterHelper() {
        return registerHelper;
    }

    public RegistryDeferredResultHelpler getRegistryDeferredResultHelpler() {
        return registryDeferredResultHelpler;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 1、RegistryCacheHelpler
        try {
            registryCacheHelpler = new RegistryCacheHelpler();
            registryCacheHelpler.start();
        } catch (Throwable e) {
            logger.error("XxlRpcRegistry - RegistryCacheHelpler: start error", e);
        }

        // 2、RegisterHelper
        try {
            registerHelper = new RegisterHelper();
            registerHelper.start();
        } catch (Throwable e) {
            logger.error("XxlRpcRegistry - RegisterHelper: start error", e);
        }

        // 3、RegistryDeferredResultHelpler
        try {
            registryDeferredResultHelpler = new RegistryDeferredResultHelpler();
            registryDeferredResultHelpler.start();
        } catch (Throwable e) {
            logger.error("XxlRpcRegistry - RegistryDeferredResultHelpler: start error", e);
        }
    }

    @Override
    public void destroy() throws Exception {
        // 1、RegistryCacheHelpler
        try {
            registryCacheHelpler.stop();
        } catch (Throwable e) {
            logger.error("XxlRpcRegistry - RegistryCacheHelpler: stop error", e);
        }

        // 2、RegisterHelper
        try {
            registerHelper.stop();
        } catch (Throwable e) {
            logger.error("XxlRpcRegistry - RegisterHelper: stop error", e);
        }

        // 3、RegistryDeferredResultHelpler
        try {
            registryDeferredResultHelpler.stop();
        } catch (Throwable e) {
            logger.error("XxlRpcRegistry - RegistryDeferredResultHelpler: stop error", e);
        }

    }

}
