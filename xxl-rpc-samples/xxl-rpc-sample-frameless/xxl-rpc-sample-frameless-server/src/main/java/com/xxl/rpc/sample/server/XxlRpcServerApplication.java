package com.xxl.rpc.sample.server;

import com.xxl.rpc.core.remoting.net.impl.netty.server.NettyServer;
import com.xxl.rpc.core.remoting.provider.XxlRpcProviderFactory;
import com.xxl.rpc.sample.api.DemoService;
import com.xxl.rpc.sample.server.service.DemoServiceImpl;
import com.xxl.rpc.core.serialize.impl.HessianSerializer;

import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2018-10-21 20:48:40
 */
public class XxlRpcServerApplication {

    public static void main(String[] args) throws Exception {

        // init
        XxlRpcProviderFactory providerFactory = new XxlRpcProviderFactory();
        providerFactory.setServer(NettyServer.class);
        providerFactory.setSerializer(HessianSerializer.class);
        providerFactory.setCorePoolSize(-1);
        providerFactory.setMaxPoolSize(-1);
        providerFactory.setIp(null);
        providerFactory.setPort(7080);
        providerFactory.setAccessToken(null);
        providerFactory.setServiceRegistry(null);
        providerFactory.setServiceRegistryParam(null);

        // add services
        providerFactory.addService(DemoService.class.getName(), null, new DemoServiceImpl());

        // start
        providerFactory.start();

        while (!Thread.currentThread().isInterrupted()) {
            TimeUnit.HOURS.sleep(1);
        }

        // stop
        providerFactory.stop();

    }

}
