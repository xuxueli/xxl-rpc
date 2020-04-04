package com.xxl.rpc.sample.server.conf;

import com.xxl.rpc.core.registry.impl.XxlRpcAdminRegister;
import com.xxl.rpc.core.remoting.net.impl.netty.server.NettyServer;
import com.xxl.rpc.core.remoting.provider.impl.XxlRpcSpringProviderFactory;
import com.xxl.rpc.core.serialize.impl.HessianSerializer;
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

    @Value("${xxl-rpc.registry.xxlrpcadmin.address}")
    private String address;

    @Value("${xxl-rpc.registry.xxlrpcadmin.env}")
    private String env;

    @Bean
    public XxlRpcSpringProviderFactory xxlRpcSpringProviderFactory() {

        XxlRpcSpringProviderFactory providerFactory = new XxlRpcSpringProviderFactory();
        providerFactory.setServer(NettyServer.class);
        providerFactory.setSerializer(HessianSerializer.class);
        providerFactory.setCorePoolSize(-1);
        providerFactory.setMaxPoolSize(-1);
        providerFactory.setIp(null);
        providerFactory.setPort(port);
        providerFactory.setAccessToken(null);
        providerFactory.setServiceRegistry(XxlRpcAdminRegister.class);
        providerFactory.setServiceRegistryParam(new HashMap<String, String>() {{
            put(XxlRpcAdminRegister.ADMIN_ADDRESS, address);
            put(XxlRpcAdminRegister.ENV, env);
        }});

        logger.info(">>>>>>>>>>> xxl-rpc provider config init finish.");
        return providerFactory;
    }

}