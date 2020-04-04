package com.xxl.rpc.sample.client.conf;

import com.xxl.rpc.core.registry.impl.XxlRpcAdminRegister;
import com.xxl.rpc.core.remoting.invoker.impl.XxlRpcSpringInvokerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * xxl-rpc invoker config
 *
 * @author xuxueli 2018-10-19
 */
@Configuration
public class XxlRpcInvokerConfig {
    private Logger logger = LoggerFactory.getLogger(XxlRpcInvokerConfig.class);


    @Value("${xxl-rpc.registry.xxlrpcadmin.address}")
    private String address;

    @Value("${xxl-rpc.registry.xxlrpcadmin.env}")
    private String env;


    @Bean
    public XxlRpcSpringInvokerFactory xxlJobExecutor() {

        XxlRpcSpringInvokerFactory invokerFactory = new XxlRpcSpringInvokerFactory();
        invokerFactory.setServiceRegistryClass(XxlRpcAdminRegister.class);
        invokerFactory.setServiceRegistryParam(new HashMap<String, String>(){{
            put(XxlRpcAdminRegister.ADMIN_ADDRESS, address);
            put(XxlRpcAdminRegister.ENV, env);
        }});

        logger.info(">>>>>>>>>>> xxl-rpc invoker config init finish.");
        return invokerFactory;
    }

}