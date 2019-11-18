package com.xxl.rpc.sample.client.config;

import com.jfinal.config.*;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.xxl.rpc.registry.impl.XxlRegistryServiceRegistry;
import com.xxl.rpc.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.remoting.invoker.annotation.XxlRpcReference;
import com.xxl.rpc.remoting.invoker.reference.XxlRpcReferenceBean;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import com.xxl.rpc.sample.client.controller.IndexController;
import com.xxl.rpc.util.XxlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author xuxueli 2018-12-21
 */
public class JFinalCoreConfig extends JFinalConfig {
	private Logger logger = LoggerFactory.getLogger(JFinalCoreConfig.class);



	// ---------------------- xxl-rpc client ----------------------

	private XxlRpcInvokerFactory xxlRpcInvokerFactory;
	private void initXxlRpcClient() throws Exception {

		// init invoker factory
		final Prop xxlJobProp = PropKit.use("xxl-rpc-sample.properties");
		xxlRpcInvokerFactory = new XxlRpcInvokerFactory(XxlRegistryServiceRegistry.class, new HashMap<String, String>(){{
			put(XxlRegistryServiceRegistry.XXL_REGISTRY_ADDRESS, xxlJobProp.get("xxl-rpc.registry.xxlregistry.address"));
			put(XxlRegistryServiceRegistry.ENV, xxlJobProp.get("xxl-rpc.registry.xxlregistry.env"));
		}});
		xxlRpcInvokerFactory.start();

		// parse reference bean
		Object bean = XxlRpcClient.instance;
		Field[] beanFields = bean.getClass().getDeclaredFields();
		if (beanFields!=null && beanFields.length>0) {
			Set<String> serviceKeyList = new HashSet<>();
			for (Field field: beanFields) {

				XxlRpcReference rpcReference = field.getAnnotation(XxlRpcReference.class);
				if (rpcReference == null) {
					continue;
				}

				// valid
				Class iface = field.getType();
				if (!iface.isInterface()) {
					throw new XxlRpcException("xxl-rpc, reference(XxlRpcReference) must be interface.");
				}

				// init reference bean
				XxlRpcReferenceBean referenceBean = new XxlRpcReferenceBean();
				referenceBean.setClient(rpcReference.client());
				referenceBean.setSerializer(rpcReference.serializer());
				referenceBean.setCallType(rpcReference.callType());
				referenceBean.setLoadBalance(rpcReference.loadBalance());
				referenceBean.setIface(iface);
				referenceBean.setVersion(rpcReference.version());
				referenceBean.setTimeout(rpcReference.timeout());
				referenceBean.setAddress(rpcReference.address());
				referenceBean.setAccessToken(rpcReference.accessToken());
				referenceBean.setInvokeCallback(null);
				referenceBean.setInvokerFactory(xxlRpcInvokerFactory);

				Object serviceProxy = referenceBean.getObject();

				// set bean
				field.setAccessible(true);
				field.set(bean, serviceProxy);

				logger.info(">>>>>>>>>>> xxl-rpc, invoker factory init reference bean success. serviceKey = {}, bean.field = {}.{}",
						XxlRpcProviderFactory.makeServiceKey(iface.getName(), rpcReference.version()), bean.getClass().getName(), field.getName());

				// collection
				String serviceKey = XxlRpcProviderFactory.makeServiceKey(iface.getName(), rpcReference.version());
				serviceKeyList.add(serviceKey);
			}
			if (serviceKeyList.size() > 0) {
				xxlRpcInvokerFactory.getServiceRegistry().discovery(serviceKeyList);
			}
		}


	}
	private void destoryXxlRpcClient() throws Exception {
		xxlRpcInvokerFactory.stop();
	}


	// ---------------------- jfinal ----------------------

    @Override
	public void configRoute(Routes route) {
		route.add("/", IndexController.class);
	}

	@Override
	public void afterJFinalStart() {
		try {
			initXxlRpcClient();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void beforeJFinalStop() {
		try {
			destoryXxlRpcClient();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
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
