package com.xxl.rpc.sample.client.conf;

import com.xxl.rpc.core.factory.config.BaseConfig;
import com.xxl.rpc.core.factory.support.XxlRpcSpringFactory;
import com.xxl.rpc.core.invoker.config.InvokerConfig;
import com.xxl.rpc.core.register.config.RegisterConfig;
import com.xxl.rpc.core.register.impl.LocalRegister;
import com.xxl.rpc.core.register.impl.XxlRpcRegister;
import com.xxl.rpc.core.register.model.RegisterInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * xxl-rpc invoker config
 *
 * @author xuxueli 2018-10-19
 */
@Configuration
public class XxlRpcInvokerConfig {
    private Logger logger = LoggerFactory.getLogger(XxlRpcInvokerConfig.class);

    @Value("${xxl-rpc.base.env}")
    private String env;

    @Value("${xxl-rpc.base.appname}")
    private String appname;


    @Value("${xxl-rpc.register.address}")
    private String address;

    @Value("${xxl-rpc.register.accesstoken}")
    private String accesstoken;


    @Bean
    public XxlRpcSpringFactory xxlRpcSpringFactory() {

        // factory
        XxlRpcSpringFactory factory = new XxlRpcSpringFactory();
        factory.setBaseConfig(new BaseConfig(env, appname));
        factory.setInvokerConfig(new InvokerConfig());
        /*factory.setRegisterConfig(new RegisterConfig(XxlRpcRegister.class, new HashMap<String, String>(){
            {
                put(XxlRpcRegister.ADMIN_ADDRESS, address);
                put(XxlRpcRegister.ACCESS_TOKEN, accesstoken);
            }
        }));*/
        factory.setRegister(new LocalRegister(new HashMap(){
            {
                RegisterInstance registerInstance = new RegisterInstance("test", "xxl-rpc-sample-springboot-server", "localhost", 7080, null);
                put(registerInstance.getAppname(), new TreeSet<>(Collections.singletonList(registerInstance)));
            }
        }));

        logger.info(">>>>>>>>>>> xxl-rpc invoker config init finish.");
        return factory;
    }

}