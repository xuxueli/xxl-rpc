package com.xxl.rpc.sample.client.conf;

import com.xxl.rpc.registry.impl.ZkServiceRegistry;
import com.xxl.rpc.remoting.invoker.impl.XxlRpcSpringInvokerFactory;
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


    @Value("${xxl-rpc.registry.zk.zkaddress:}")
    private String zkaddress;

    @Value("${xxl-rpc.registry.zk.zkdigest:}")
    private String zkdigest;

    @Value("${xxl-rpc.env:}")
    private String env;


    @Bean
    public XxlRpcSpringInvokerFactory xxlJobExecutor() {

        XxlRpcSpringInvokerFactory invokerFactory = new XxlRpcSpringInvokerFactory();
        if (zkaddress!=null && zkaddress.trim().length()>0) {
            invokerFactory.setServiceRegistryClass(ZkServiceRegistry.class);
            invokerFactory.setServiceRegistryParam(new HashMap<String, String>(){{
                put(ZkServiceRegistry.ZK_ADDRESS, zkaddress);
                put(ZkServiceRegistry.ZK_DIGEST, zkdigest);
                put(ZkServiceRegistry.ENV, env);
            }});
        }

        logger.info(">>>>>>>>>>> xxl-rpc invoker config init finish.");
        return invokerFactory;
    }

}