package com.xxl.rpc.sample.client.conf;

import com.xxl.rpc.core.boot.config.BaseConfig;
import com.xxl.rpc.core.boot.support.SpringXxlRpcBootstrap;
import com.xxl.rpc.core.invoker.config.InvokerConfig;
import com.xxl.rpc.core.provider.config.ProviderConfig;
import com.xxl.rpc.core.register.impl.XxlConfRegister;
import com.xxl.rpc.core.remoting.Client;
import com.xxl.rpc.core.remoting.Server;
import com.xxl.rpc.core.remoting.impl.netty.client.NettyClient;
import com.xxl.rpc.core.serializer.Serializer;
import com.xxl.rpc.core.serializer.impl.JsonbSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * xxl-rpc invoker config
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

    @Value("${xxl.rpc.provider.enable}")
    private boolean providerEnable;

    @Value("${xxl.rpc.invoker.enable}")
    private boolean invokerEnable;

    @Value("${xxl.rpc.invoker.client}")
    private Class<? extends Client> client;

    @Value("${xxl.rpc.invoker.serializer}")
    private Class<? extends Serializer> serializer;

    @Value("${xxl.rpc.invoker.serializerAllowPackageList}")
    private String[] serializerAllowPackageList;


    @Bean
    public SpringXxlRpcBootstrap xxlRpcSpringFactory() {

        // XxlRpc Bootstrap
        SpringXxlRpcBootstrap factory = new SpringXxlRpcBootstrap();
        factory.setBaseConfig(new BaseConfig(env, appname));
        factory.setRegister(new XxlConfRegister(xxlConfAddress, xxlConfAccesstoken));
        factory.setInvokerConfig(new InvokerConfig(invokerEnable, client, serializer, Arrays.asList(serializerAllowPackageList)));
        factory.setProviderConfig(new ProviderConfig(providerEnable));

        return factory;
    }

}