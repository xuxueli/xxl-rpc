package com.xxl.rpc.sample.client.conf;

import com.xxl.rpc.core.boot.config.BaseConfig;
import com.xxl.rpc.core.boot.support.XxlRpcSpringFactory;
import com.xxl.rpc.core.invoker.config.InvokerConfig;
import com.xxl.rpc.core.register.impl.XxlRpcRegister;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-rpc invoker config
 *
 * @author xuxueli 2018-10-19
 */
@Configuration
public class XxlRpcBootstrapConf {

    @Value("${xxl-rpc.base.env}")
    private String env;

    @Value("${xxl-rpc.base.appname}")
    private String appname;

    @Value("${xxl-rpc.register.address}")
    private String address;

    @Value("${xxl-rpc.register.accesstoken}")
    private String accesstoken;

    @Value("${xxl-rpc.invoker.open}")
    private boolean open;

    @Bean
    public XxlRpcSpringFactory xxlRpcSpringFactory() {

        // XxlRpcSpringFactory
        XxlRpcSpringFactory factory = new XxlRpcSpringFactory();
        factory.setBaseConfig(new BaseConfig(env, appname));
        factory.setRegister(new XxlRpcRegister(address, accesstoken));
        factory.setInvokerConfig(new InvokerConfig(open));

        return factory;
    }

}