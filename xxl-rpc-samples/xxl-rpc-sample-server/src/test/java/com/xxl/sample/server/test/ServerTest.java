package com.xxl.sample.server.test;

import com.xxl.rpc.registry.ServiceRegistry;
import com.xxl.rpc.remoting.net.NetEnum;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import com.xxl.rpc.sample.api.DemoService;
import com.xxl.rpc.sample.server.service.DemoServiceImpl;
import com.xxl.rpc.serialize.Serializer;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2018-10-21 20:48:40
 */
public class ServerTest {

    public static void main(String[] args) throws Exception {

        // init
        XxlRpcProviderFactory providerFactory = new XxlRpcProviderFactory();
        providerFactory.initConfig(NetEnum.JETTY, Serializer.SerializeEnum.HESSIAN.getSerializer(), null, 7080, null, null, null);

        // add services
        providerFactory.addService(DemoService.class.getName(), null, new DemoServiceImpl());

        // start
        providerFactory.start();

        TimeUnit.MINUTES.sleep(5);

        // stop
        providerFactory.stop();
    }

}
