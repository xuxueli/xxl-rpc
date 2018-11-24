package com.xxl.rpc.sample.server.conf;

import com.xxl.rpc.registry.impl.NativeServiceRegistry;
import com.xxl.rpc.remoting.provider.impl.XxlRpcSpringProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * xxl-rpc provider config
 *
 * @author xuxueli 2018-10-19
 */
@Configuration
public class XxlRpcProviderConfig {
    private Logger logger = LoggerFactory.getLogger(XxlRpcProviderConfig.class);

    @Value("${xxl-rpc.remoting.port}")
    private int port;

    @Value("${xxl-rpc.registry.native.adminaddress}")
    private String adminaddress;

    @Value("${xxl-rpc.registry.native.env}")
    private String env;

    @Bean
    public XxlRpcSpringProviderFactory xxlRpcSpringProviderFactory() {

        XxlRpcSpringProviderFactory providerFactory = new XxlRpcSpringProviderFactory();
        providerFactory.setPort(port);
        providerFactory.setServiceRegistryClass(NativeServiceRegistry.class);
        providerFactory.setServiceRegistryParam(new HashMap<String, String>(){{
            put(NativeServiceRegistry.XXL_RPC_ADMIN, adminaddress);
            put(NativeServiceRegistry.ENV, env);
        }});

        logger.info(">>>>>>>>>>> xxl-rpc provider config init finish.");
        return providerFactory;
    }

}