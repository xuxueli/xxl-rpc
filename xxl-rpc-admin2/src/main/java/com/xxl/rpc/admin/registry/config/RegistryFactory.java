package com.xxl.rpc.admin.registry.config;

import com.xxl.rpc.admin.mapper.*;
import com.xxl.rpc.admin.registry.thread.AccessTokenHelpler;
import com.xxl.rpc.admin.registry.thread.RegisterHelper;
import com.xxl.rpc.admin.registry.thread.RegistryCacheHelpler;
import com.xxl.rpc.admin.registry.thread.RegistryDeferredResultHelpler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * registry config
 *
 * @author xuxueli
 */
@Configuration
public class RegistryFactory implements InitializingBean, DisposableBean {
    private static Logger logger = LoggerFactory.getLogger(RegistryFactory.class);

    // ---------------------- instance ----------------------
    private static RegistryFactory instance;
    public static RegistryFactory getInstance() {
        return instance;
    }

    // ---------------------- helper ----------------------
    @Resource
    private InstanceMapper instanceMapper;
    @Resource
    private ApplicationMapper applicationMapper;
    @Resource
    private EnvironmentMapper environmentMapper;
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private AccessTokenMapper accessTokenMapper;

    public InstanceMapper getInstanceMapper() {
        return instanceMapper;
    }
    public ApplicationMapper getApplicationMapper() {
        return applicationMapper;
    }
    public EnvironmentMapper getEnvironmentMapper() {
        return environmentMapper;
    }
    public MessageMapper getMessageMapper() {
        return messageMapper;
    }
    public AccessTokenMapper getAccessTokenMapper() {
        return accessTokenMapper;
    }

    // ---------------------- helper ----------------------

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

    /**
     * 4、AccessTokenHelpler
     */
    private AccessTokenHelpler accessTokenHelpler;


    public RegistryCacheHelpler getRegistryCacheHelpler() {
        return registryCacheHelpler;
    }

    public RegisterHelper getRegisterHelper() {
        return registerHelper;
    }

    public RegistryDeferredResultHelpler getRegistryDeferredResultHelpler() {
        return registryDeferredResultHelpler;
    }

    public AccessTokenHelpler getAccessTokenHelpler() {
        return accessTokenHelpler;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // base init
        instance = this;

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

        // 4、AccessTokenHelpler
        try {
            accessTokenHelpler = new AccessTokenHelpler();
            accessTokenHelpler.start();
        } catch (Throwable e) {
            logger.error("XxlRpcRegistry - AccessTokenHelpler: start error", e);
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

        // 4、AccessTokenHelpler
        try {
            accessTokenHelpler.stop();
        } catch (Throwable e) {
            logger.error("XxlRpcRegistry - AccessTokenHelpler: stop error", e);
        }

    }

}
