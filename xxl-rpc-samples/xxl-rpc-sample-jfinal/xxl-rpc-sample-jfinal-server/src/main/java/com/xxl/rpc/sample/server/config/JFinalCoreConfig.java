package com.xxl.rpc.sample.server.config;

import com.jfinal.config.*;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.xxl.rpc.registry.impl.XxlRegistryServiceRegistry;
import com.xxl.rpc.remoting.net.NetEnum;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import com.xxl.rpc.sample.api.DemoService;
import com.xxl.rpc.sample.server.service.DemoServiceImpl;
import com.xxl.rpc.serialize.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * @author xuxueli 2018-12-21
 */
public class JFinalCoreConfig extends JFinalConfig {
	private Logger logger = LoggerFactory.getLogger(JFinalCoreConfig.class);



	// ---------------------- xxl-rpc server ----------------------

	private XxlRpcProviderFactory xxlRpcProviderFactory;
	private void initXxlRpcServer() throws Exception {

		// init invoker factory
		final Prop xxlJobProp = PropKit.use("xxl-rpc-sample.properties");
		xxlRpcProviderFactory = new XxlRpcProviderFactory();
		xxlRpcProviderFactory.initConfig(NetEnum.NETTY, Serializer.SerializeEnum.HESSIAN.getSerializer(), null, 7080, null, XxlRegistryServiceRegistry.class, new HashMap<String, String>(){{
			put(XxlRegistryServiceRegistry.XXL_REGISTRY_ADDRESS, xxlJobProp.get("xxl-rpc.registry.xxlregistry.address"));
			put(XxlRegistryServiceRegistry.ENV, xxlJobProp.get("xxl-rpc.registry.xxlregistry.env"));
		}});

		// add services
		xxlRpcProviderFactory.addService(DemoService.class.getName(), null, new DemoServiceImpl());

		// start
		xxlRpcProviderFactory.start();
	}
	private void destoryXxlRpcServer() throws Exception {
		xxlRpcProviderFactory.stop();
	}


	// ---------------------- jfinal ----------------------

	@Override
	public void afterJFinalStart() {
		try {
			initXxlRpcServer();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void beforeJFinalStop() {
		try {
			destoryXxlRpcServer();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void configRoute(Routes routes) {

	}

	public void configConstant(Constants constants) {

	}

	public void configPlugin(Plugins plugins) {

	}

	public void configInterceptor(Interceptors interceptors) {

	}

	public void configHandler(Handlers handlers) {

	}


}
