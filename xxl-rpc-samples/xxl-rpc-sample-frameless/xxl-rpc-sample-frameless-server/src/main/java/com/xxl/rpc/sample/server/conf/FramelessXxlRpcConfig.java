package com.xxl.rpc.sample.server.conf;

import com.xxl.rpc.core.boot.XxlRpcBootstrap;
import com.xxl.rpc.core.boot.config.BaseConfig;
import com.xxl.rpc.core.provider.config.ProviderConfig;
import com.xxl.rpc.core.remoting.Server;
import com.xxl.rpc.core.serializer.Serializer;
import com.xxl.rpc.sample.api.DemoService;
import com.xxl.rpc.sample.server.service.DemoServiceImpl;
import com.xxl.rpc.sample.server.service.generic.Demo2Service;
import com.xxl.rpc.sample.server.service.generic.Demo2ServiceImpl;
import com.xxl.tool.core.PropTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;

public class FramelessXxlRpcConfig {
    private static Logger logger = LoggerFactory.getLogger(FramelessXxlRpcConfig.class);


    private static FramelessXxlRpcConfig instance = new FramelessXxlRpcConfig();
    public static FramelessXxlRpcConfig getInstance() {
        return instance;
    }


    /**
     * xxl-rpc bootstrap
     */
    private XxlRpcBootstrap rpcBootstrap;

    /**
     * start
     */
    @SuppressWarnings("unchecked")
    public void start() throws ClassNotFoundException {

        // load prop
        Properties xxlRpcProp = PropTool.loadProp("xxl-rpc.properties");

        // 1、init XxlRpcBootstrap
        XxlRpcBootstrap rpcBootstrap = new XxlRpcBootstrap();
        rpcBootstrap.setBaseConfig(new BaseConfig(
                PropTool.getString(xxlRpcProp, "xxl.rpc.base.env"),
                PropTool.getString(xxlRpcProp, "xxl.rpc.base.appname")
        ));
        rpcBootstrap.setProviderConfig(
                new ProviderConfig(
                        PropTool.getBoolean(xxlRpcProp, "xxl.rpc.provider.enable"),
                        (Class<? extends Server>) Class.forName(PropTool.getString(xxlRpcProp, "xxl.rpc.provider.server")),
                        (Class<? extends Serializer>) Class.forName(PropTool.getString(xxlRpcProp, "xxl.rpc.provider.serializer")),
                        Arrays.asList(PropTool.getString(xxlRpcProp, "xxl.rpc.provider.serializerAllowPackageList").split(",")),
                        PropTool.getInt(xxlRpcProp, "xxl.rpc.provider.port"),
                        PropTool.getInt(xxlRpcProp, "xxl.rpc.provider.corePoolSize"),
                        PropTool.getInt(xxlRpcProp, "xxl.rpc.provider.maxPoolSize"),
                        PropTool.getString(xxlRpcProp, "xxl.rpc.provider.address"))
        );

        // 2、start
        rpcBootstrap.start();

        // 3、registry biz services
        rpcBootstrap.getProvider().addService(DemoService.class.getName(), null, new DemoServiceImpl());
        rpcBootstrap.getProvider().addService(Demo2Service.class.getName(), null, new Demo2ServiceImpl());
    }

    /**
     * stop
     */
    public void stop() throws Exception {
        // 4、stop
        if (rpcBootstrap != null) {
            rpcBootstrap.stop();
        }
    }

}
