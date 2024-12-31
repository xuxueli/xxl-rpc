package com.xxl.rpc.sample.server.conf;

import com.xxl.rpc.core.boot.config.BaseConfig;
import com.xxl.rpc.core.boot.support.SpringXxlRpcBootstrap;
import com.xxl.rpc.core.invoker.config.InvokerConfig;
import com.xxl.rpc.core.provider.config.ProviderConfig;
import com.xxl.rpc.core.register.impl.XxlRpcAdminRegister;
import com.xxl.rpc.core.remoting.impl.netty.server.NettyServer;
import com.xxl.rpc.core.serializer.impl.JsonbSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-rpc provider config
 *
 * @author xuxueli 2018-10-19
 */
@Configuration
public class SpringXxlRpcBootstrapConfig {

    @Value("${xxl-rpc.base.env}")
    private String env;

    @Value("${xxl-rpc.base.appname}")
    private String appname;

    @Value("${xxl-rpc.register.address}")
    private String address;

    @Value("${xxl-rpc.register.accesstoken}")
    private String accesstoken;

    @Value("${xxl-rpc.invoker.open}")
    private boolean invokerOpen;

    @Value("${xxl-rpc.provider.open}")
    private boolean providerOpen;

    @Value("${xxl-rpc.provider.port}")
    private int port;

    @Value("${xxl-rpc.provider.corePoolSize}")
    private int corePoolSize;

    @Value("${xxl-rpc.provider.maxPoolSize}")
    private int maxPoolSize;

    
    @Bean
    public SpringXxlRpcBootstrap xxlRpcSpringFactory() {

        // XxlRpc Bootstrap
        SpringXxlRpcBootstrap factory = new SpringXxlRpcBootstrap();
        factory.setBaseConfig(new BaseConfig(env, appname));
        factory.setRegister(new XxlRpcAdminRegister(address, accesstoken));
        factory.setInvokerConfig(new InvokerConfig(invokerOpen));
        factory.setProviderConfig(providerOpen ?
                new ProviderConfig(
                        NettyServer.class,
                        JsonbSerializer.class,
                        null,
                        port,
                        corePoolSize,
                        maxPoolSize,
                        null) : new ProviderConfig(providerOpen));

        return factory;
    }

}