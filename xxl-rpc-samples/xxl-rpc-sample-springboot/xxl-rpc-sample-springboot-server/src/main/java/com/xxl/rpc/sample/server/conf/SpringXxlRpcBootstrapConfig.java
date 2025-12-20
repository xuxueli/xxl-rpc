package com.xxl.rpc.sample.server.conf;

import com.xxl.rpc.core.boot.config.BaseConfig;
import com.xxl.rpc.core.boot.support.SpringXxlRpcBootstrap;
import com.xxl.rpc.core.invoker.config.InvokerConfig;
import com.xxl.rpc.core.provider.config.ProviderConfig;
import com.xxl.rpc.core.register.impl.XxlConfRegister;
import com.xxl.rpc.core.remoting.Server;
import com.xxl.rpc.core.serializer.Serializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * xxl-rpc provider config
 *
 * @author xuxueli 2018-10-19
 */
@Configuration
public class SpringXxlRpcBootstrapConfig {

    @Value("${xxl.rpc.base.env}")
    private String env;

    @Value("${xxl.rpc.base.appname}")
    private String appname;

    @Value("${xxl.conf.admin.address}")
    private String xxlConfAddress;

    @Value("${xxl.conf.admin.accesstoken}")
    private String xxlConfAccesstoken;

    @Value("${xxl.rpc.invoker.enable}")
    private boolean invokerEnable;

    @Value("${xxl.rpc.provider.enable}")
    private boolean providerEnable;

    @Value("${xxl.rpc.provider.server}")
    private Class<? extends Server> server;

    @Value("${xxl.rpc.provider.serializer}")
    private Class<? extends Serializer> serializer;

    @Value("${xxl.rpc.provider.serializerAllowPackageList}")
    private String[] serializerAllowPackageList;

    @Value("${xxl.rpc.provider.port}")
    private int port;

    @Value("${xxl.rpc.provider.corePoolSize}")
    private int corePoolSize;

    @Value("${xxl.rpc.provider.maxPoolSize}")
    private int maxPoolSize;

    @Value("${xxl.rpc.provider.address}")
    private String address;

    
    @Bean
    public SpringXxlRpcBootstrap xxlRpcSpringFactory() {

        // XxlRpc Bootstrap
        SpringXxlRpcBootstrap factory = new SpringXxlRpcBootstrap();
        factory.setBaseConfig(new BaseConfig(env, appname));
        factory.setRegister(new XxlConfRegister(xxlConfAddress, xxlConfAccesstoken));
        factory.setInvokerConfig(new InvokerConfig(invokerEnable));
        factory.setProviderConfig(
                new ProviderConfig(
                        providerEnable,
                        server,
                        serializer,
                        Arrays.asList(serializerAllowPackageList),
                        port,
                        corePoolSize,
                        maxPoolSize,
                        address));

        return factory;
    }

}