package com.xxl.rpc.sample.server.conf;

import com.xxl.rpc.core.boot.config.BaseConfig;
import com.xxl.rpc.core.boot.support.XxlRpcSpringFactory;
import com.xxl.rpc.core.provider.config.ProviderConfig;
import com.xxl.rpc.core.remoting.impl.netty.server.NettyServer;
import com.xxl.rpc.core.serializer.impl.JsonbSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-rpc provider config
 *
 * @author xuxueli 2018-10-19
 */
@Configuration
public class XxlRpcProviderConfig {
    private Logger logger = LoggerFactory.getLogger(XxlRpcProviderConfig.class);

    @Value("${xxl-rpc.base.env}")
    private String env;

    @Value("${xxl-rpc.base.appname}")
    private String appname;

    @Value("${xxl-rpc.provider.port}")
    private int port;

    @Value("${xxl-rpc.register.address}")
    private String address;

    @Value("${xxl-rpc.register.accesstoken}")
    private String accesstoken;

    @Bean
    public XxlRpcSpringFactory xxlRpcSpringFactory() {

        // init
        XxlRpcSpringFactory factory = new XxlRpcSpringFactory();
        factory.setBaseConfig(new BaseConfig(env, appname));
        /*factory.setRegisterConfig(new RegisterConfig(XxlRpcRegister.class, new HashMap<String, String>(){
            {
                put(XxlRpcRegister.ADMIN_ADDRESS, address);
                put(XxlRpcRegister.ACCESS_TOKEN, accesstoken);
            }
        }));*/
        factory.setProviderConfig(new ProviderConfig(
                NettyServer.class,
                JsonbSerializer.class,
                port,
                -1,
                -1,
                null));

        logger.info(">>>>>>>>>>> xxl-rpc provider config init finish.");
        return factory;
    }

}