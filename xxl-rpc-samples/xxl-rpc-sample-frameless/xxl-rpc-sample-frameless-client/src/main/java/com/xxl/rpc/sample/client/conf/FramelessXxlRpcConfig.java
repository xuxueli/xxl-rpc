package com.xxl.rpc.sample.client.conf;

import com.xxl.rpc.core.boot.XxlRpcBootstrap;
import com.xxl.rpc.core.boot.config.BaseConfig;
import com.xxl.rpc.core.invoker.call.CallType;
import com.xxl.rpc.core.invoker.config.InvokerConfig;
import com.xxl.rpc.core.invoker.reference.XxlRpcReferenceBean;
import com.xxl.rpc.core.invoker.route.LoadBalance;
import com.xxl.rpc.core.register.entity.RegisterInstance;
import com.xxl.rpc.core.register.impl.LocalRegister;
import com.xxl.rpc.core.remoting.Client;
import com.xxl.rpc.core.serializer.Serializer;
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
    private volatile XxlRpcBootstrap rpcBootstrap;

    /**
     * build referenceBean
     */
    public <T> T buildReferenceBean(CallType callType, Class<T> serviceCLass) throws Exception {
        XxlRpcReferenceBean referenceBean = new XxlRpcReferenceBean();
        referenceBean.setCallType(callType);
        referenceBean.setLoadBalance(LoadBalance.ROUND);
        referenceBean.setIface(serviceCLass);
        referenceBean.setVersion(null);
        referenceBean.setTimeout(500);
        referenceBean.setAppname("xxl-rpc-sample-frameless-server");
        referenceBean.setRpcBootstrap(this.rpcBootstrap);

        return (T) referenceBean.getObject();
    }

    /**
     * start
     */
    @SuppressWarnings("unchecked")
    public void start() throws ClassNotFoundException {

        // load prop
        Properties xxlRpcProp = PropTool.loadProp("xxl-rpc.properties");

        // 1、LocalRegister : Mock 注册数据
        LocalRegister localRegister = new LocalRegister();
        localRegister.register(new RegisterInstance("test", "xxl-rpc-sample-frameless-server", "127.0.0.1", 7080, null));

        // 2、XxlRpcBootstrap
        rpcBootstrap = new XxlRpcBootstrap();
        rpcBootstrap.setBaseConfig(new BaseConfig(
                PropTool.getString(xxlRpcProp, "xxl.rpc.base.env"),
                PropTool.getString(xxlRpcProp, "xxl.rpc.base.appname")
        ));
        rpcBootstrap.setRegister(localRegister);
        rpcBootstrap.setInvokerConfig(
                new InvokerConfig(
                        PropTool.getBoolean(xxlRpcProp, "xxl.rpc.invoker.enable"),
                        (Class<? extends Client>) Class.forName(PropTool.getString(xxlRpcProp, "xxl.rpc.invoker.client")),
                        (Class<? extends Serializer>) Class.forName(PropTool.getString(xxlRpcProp, "xxl.rpc.invoker.serializer")),
                        Arrays.asList(PropTool.getString(xxlRpcProp, "xxl.rpc.invoker.serializerAllowPackageList").split(",")))
        );

        // 3、start
        rpcBootstrap.start();
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
