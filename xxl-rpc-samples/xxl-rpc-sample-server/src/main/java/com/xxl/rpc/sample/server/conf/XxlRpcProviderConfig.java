package com.xxl.rpc.sample.server.conf;

import com.xxl.rpc.remoting.net.NetEnum;
import com.xxl.rpc.remoting.provider.impl.XxlRpcSpringProviderFactory;
import com.xxl.rpc.serialize.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xuxueli 2018-10-19
 */
@Configuration
public class XxlRpcProviderConfig {
    private Logger logger = LoggerFactory.getLogger(XxlRpcProviderConfig.class);


    @Bean
    public XxlRpcSpringProviderFactory xxlJobExecutor() {

        logger.info(">>>>>>>>>>> xxl-rpc provider config init.");
        XxlRpcSpringProviderFactory providerFactory = new XxlRpcSpringProviderFactory();
        providerFactory.setNetType(NetEnum.JETTY.name());
        providerFactory.setSerialize(Serializer.SerializeEnum.HESSIAN.name());
        //providerFactory.setIp(null);
        providerFactory.setPort(7080);
        providerFactory.setAccessToken(null);
        //providerFactory.setServiceRegistry(ServiceRegistry.ServiceRegistryEnum.ZK.name());
        //providerFactory.setServiceRegistryClass(ServiceRegistry.ServiceRegistryEnum.ZK.serviceRegistryClass);
        //providerFactory.setServiceRegistryParam(null);

        return providerFactory;
    }

}