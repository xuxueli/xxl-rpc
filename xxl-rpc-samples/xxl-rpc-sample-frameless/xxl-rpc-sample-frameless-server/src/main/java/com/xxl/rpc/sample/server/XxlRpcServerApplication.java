package com.xxl.rpc.sample.server;

import com.xxl.rpc.core.boot.XxlRpcBootstrap;
import com.xxl.rpc.core.boot.config.BaseConfig;
import com.xxl.rpc.core.provider.config.ProviderConfig;
import com.xxl.rpc.core.remoting.impl.netty.server.NettyServer;
import com.xxl.rpc.sample.api.DemoService;
import com.xxl.rpc.sample.server.service.DemoServiceImpl;
import com.xxl.rpc.core.serializer.impl.JsonbSerializer;

import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2018-10-21 20:48:40
 */
public class XxlRpcServerApplication {

    public static void main(String[] args) throws Exception {

        // init
        XxlRpcBootstrap rpcBootstrap = new XxlRpcBootstrap();
        rpcBootstrap.setBaseConfig(new BaseConfig("test", "client01"));
        rpcBootstrap.setProviderConfig(new ProviderConfig(NettyServer.class, JsonbSerializer.class, -1, -1, 7080, null));

        // start
        rpcBootstrap.start();

        // add services
        rpcBootstrap.getProvider().addService(DemoService.class.getName(), null, new DemoServiceImpl());


        while (!Thread.currentThread().isInterrupted()) {
            TimeUnit.HOURS.sleep(1);
        }

        // stop
        rpcBootstrap.stop();

    }

}
