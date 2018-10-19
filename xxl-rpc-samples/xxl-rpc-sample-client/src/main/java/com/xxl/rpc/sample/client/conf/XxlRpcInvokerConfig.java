package com.xxl.rpc.sample.client.conf;

import com.xxl.rpc.remoting.invoker.impl.XxlRpcSpringInvokerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xuxueli 2018-10-19
 */
@Configuration
public class XxlRpcInvokerConfig {
    private Logger logger = LoggerFactory.getLogger(XxlRpcInvokerConfig.class);


    @Bean
    public XxlRpcSpringInvokerFactory xxlJobExecutor() {

        logger.info(">>>>>>>>>>> xxl-rpc provider config init.");
        XxlRpcSpringInvokerFactory invokerFactory = new XxlRpcSpringInvokerFactory();

        /*invokerFactory.setServiceRegistry(ServiceRegistry.ServiceRegistryEnum.ZK.name());
        invokerFactory.setServiceRegistryClass(ServiceRegistry.ServiceRegistryEnum.ZK.serviceRegistryClass);
        invokerFactory.setServiceRegistryParam(null);*/

        return invokerFactory;
    }

}