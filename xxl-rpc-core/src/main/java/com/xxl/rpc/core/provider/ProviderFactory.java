package com.xxl.rpc.core.provider;

import com.alibaba.fastjson2.JSON;
import com.xxl.rpc.core.boot.XxlRpcBootstrap;
import com.xxl.rpc.core.invoker.generic.XxlRpcGenericService;
import com.xxl.rpc.core.register.entity.RegisterInstance;
import com.xxl.rpc.core.remoting.Server;
import com.xxl.rpc.core.remoting.entity.XxlRpcRequest;
import com.xxl.rpc.core.remoting.entity.XxlRpcResponse;
import com.xxl.rpc.core.serializer.Serializer;
import com.xxl.rpc.core.util.BeanTool;
import com.xxl.rpc.core.util.ClassUtil;
import com.xxl.rpc.core.util.XxlRpcException;
import com.xxl.tool.exception.ThrowableTool;
import com.xxl.tool.http.IPTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * provider
 *
 * @author xuxueli 2015-10-31 22:54:27
 */
public class ProviderFactory {
	private static final Logger logger = LoggerFactory.getLogger(ProviderFactory.class);

	/**
	 * factory link
	 */
	private final XxlRpcBootstrap rpcBootstrap;

	public ProviderFactory(final XxlRpcBootstrap rpcBootstrap) {
		this.rpcBootstrap = rpcBootstrap;
	}


	// ---------------------- start / stop ----------------------

	/**
	 * Server instance
	 */
	private Server serverInstance;

	/**
	 * Serializer instance
	 */
	private Serializer serializerInstance;

	public Serializer getSerializerInstance() {
		return serializerInstance;
	}

	/**
	 * start
	 *
	 * @throws Exception
	 */
	public void start() throws Exception {
		// valid
		if (rpcBootstrap.getProviderConfig().getServer() == null) {
			throw new XxlRpcException("xxl-rpc provider server missing.");
		}
		if (rpcBootstrap.getProviderConfig().getSerializer()==null) {
			throw new XxlRpcException("xxl-rpc provider serializer missing.");
		}
		if (!(rpcBootstrap.getProviderConfig().getCorePoolSize()>0
				&& rpcBootstrap.getProviderConfig().getMaxPoolSize()>0
				&& rpcBootstrap.getProviderConfig().getMaxPoolSize()>= rpcBootstrap.getProviderConfig().getCorePoolSize())) {
			rpcBootstrap.getProviderConfig().setCorePoolSize(60);
			rpcBootstrap.getProviderConfig().setMaxPoolSize(300);
		}

		// parse address
		String ip = IPTool.getIp();
		if (rpcBootstrap.getProviderConfig().getPort() <= 0) {
			rpcBootstrap.getProviderConfig().setPort(7080);
		}
		if (rpcBootstrap.getProviderConfig().getAddress()==null || rpcBootstrap.getProviderConfig().getAddress().isEmpty()) {
			String address = IPTool.toAddressString(IPTool.toAddress(ip, rpcBootstrap.getProviderConfig().getPort()));	//IpUtil.getIpPort(ip, rpcBootstrap.getProviderConfig().getPort());
			rpcBootstrap.getProviderConfig().setAddress(address);
		}
		if (IPTool.isPortInUsed(rpcBootstrap.getProviderConfig().getPort())) {		// NetUtil.isPortUsed(rpcBootstrap.getProviderConfig().getPort())
			throw new XxlRpcException("xxl-rpc provider port["+ rpcBootstrap.getProviderConfig().getPort() +"] is used.");
		}

		// 1、serializer init
		this.serializerInstance = rpcBootstrap.getProviderConfig().getSerializer().newInstance();
		this.serializerInstance.allowPackageList(rpcBootstrap.getProviderConfig().getSerializerAllowPackageList());

		// 2、server init
		this.serverInstance = rpcBootstrap.getProviderConfig().getServer().newInstance();
		this.serverInstance.setStartedCallback(new Callable<Void>() {		// serviceRegistry started
			public Void call() throws Exception {
				// 3.1、register init
				if (rpcBootstrap.getRegister() != null) {
					RegisterInstance instance = new RegisterInstance();
					instance.setEnv(rpcBootstrap.getBaseConfig().getEnv());
					instance.setAppname(rpcBootstrap.getBaseConfig().getAppname());
					instance.setIp(ip);
					instance.setPort(rpcBootstrap.getProviderConfig().getPort());
					//instance.setExtendInfo(null);

					rpcBootstrap.getRegister().register(instance);
				}
                return null;
            }
		});
		serverInstance.setStopedCallback(new Callable<Void>() {		// serviceRegistry stoped
			public Void call() throws Exception {
				// 3.2、register stop
				if (rpcBootstrap.getRegister() != null) {
					RegisterInstance instance = new RegisterInstance();
					instance.setEnv(rpcBootstrap.getBaseConfig().getEnv());
					instance.setAppname(rpcBootstrap.getBaseConfig().getAppname());
					instance.setIp(ip);
					instance.setPort(rpcBootstrap.getProviderConfig().getPort());
					//instance.setExtendInfo(null);

					rpcBootstrap.getRegister().unregister(instance);
				}
				return null;
			}
		});
		serverInstance.start(rpcBootstrap);
	}

	/**
	 * stop
	 *
	 * @throws Exception
	 */
	public void  stop() throws Exception {
		// stop server
		serverInstance.stop();
	}


	// ---------------------- service store ----------------------

	/**
	 * init local rpc service map
	 */
	private volatile Map<String, Object> serviceInstanceStore = new HashMap<>();
	public Map<String, Object> getServiceInstanceStore() {
		return serviceInstanceStore;
	}

	/**
	 * make service key
	 *
	 * @param iface
	 * @param version
	 * @return
	 */
	public static String makeServiceKey(String iface, String version){
		String serviceKey = iface;
		if (version!=null && version.trim().length()>0) {
			serviceKey += "#".concat(version);
		}
		return serviceKey;
	}

	/**
	 * add service
	 *
	 * @param iface
	 * @param version
	 * @param serviceBean
	 */
	public void addService(String iface, String version, Object serviceBean){
		String serviceKey = makeServiceKey(iface, version);
		serviceInstanceStore.put(serviceKey, serviceBean);

		logger.info(">>>>>>>>>>> xxl-rpc, provider factory add service success. serviceKey = {}, serviceBean = {}", serviceKey, serviceBean.getClass());
	}


	// ---------------------- service invoke ----------------------

	/**
	 * invoke service
	 *
	 * @param xxlRpcRequest  request
	 * @return  response
	 */
	public XxlRpcResponse invokeService(XxlRpcRequest xxlRpcRequest) {

		//  make response
		XxlRpcResponse xxlRpcResponse = new XxlRpcResponse();
		xxlRpcResponse.setRequestId(xxlRpcRequest.getRequestId());

        // invoke
		try {
            // 1、generic invoke
            if (XxlRpcGenericService.class.getName().equals(xxlRpcRequest.getClassName()) && "$invoke".equals(xxlRpcRequest.getMethodName())) {

                // parse generic-param
                String className = (String) xxlRpcRequest.getParameters()[0];
                String version = (String) xxlRpcRequest.getParameters()[1];
                String methodName = (String) xxlRpcRequest.getParameters()[2];
                Object parameterTypesOrigin = xxlRpcRequest.getParameters()[3];
                Object parametersOrigin = xxlRpcRequest.getParameters()[4];

                // match service bean
                String serviceKey = makeServiceKey(className, version);
                Object serviceBean = serviceInstanceStore.get(serviceKey);
                if (serviceBean == null) {
                    xxlRpcResponse.setErrorMsg("The serviceKey["+ serviceKey +"] not found.");
                    return xxlRpcResponse;
                }

                // parse serviceClass
                Class<?> serviceClass = serviceBean.getClass();

                // parse parameterTypes + parameters
                Class<?>[] parameterTypes = null;
                Object[] parameters = null;
                if (parameterTypesOrigin instanceof List<?> parameterTypes_list
                        && parametersOrigin instanceof List<?> parameters_list) {
                    if (parameterTypes_list.size() != parameters_list.size()) {
                        xxlRpcResponse.setErrorMsg("The parameterTypes size["+ parameterTypes_list.size() +"] is not equals parameters size["+ parameters_list.size() +"].");
                        return xxlRpcResponse;
                    }
                    // init
                    parameterTypes = new Class[parameterTypes_list.size()];
                    parameters = new Object[parameters_list.size()];
                    // parse item
                    for (int i = 0; i < parameterTypes_list.size(); i++) {
                        parameterTypes[i] = ClassUtil.resolveClass(String.valueOf(parameterTypes_list.get(i)));
                        parameters[i] = BeanTool.primitiveToTargetClass(parameters_list.get(i), parameterTypes[i]);
                        //parameters[i] = BeanTool.primitiveToTargetClass(parameters_list.get(i), parameterTypes[i]);     // parameterTypes[i].isArray();  not support list
                    }
                }

                // invoke
                Method method = serviceClass.getMethod(methodName, parameterTypes);
                method.setAccessible(true);
                Object result = method.invoke(serviceBean, parameters);

                // parse generic-result
                if (result!=null) {
                    //result = BeanTool.objectToPrimitive(result);
                    result = JSON.toJSONString(result);
                }

                // write result
                xxlRpcResponse.setResult(result);
                return xxlRpcResponse;
            }

            // 2、default invoke

            // match service bean
            String serviceKey = makeServiceKey(xxlRpcRequest.getClassName(), xxlRpcRequest.getVersion());
            Object serviceBean = serviceInstanceStore.get(serviceKey);
            if (serviceBean == null) {
                xxlRpcResponse.setErrorMsg("The serviceKey["+ serviceKey +"] not found.");
                return xxlRpcResponse;
            }

			// parse param
			Class<?> serviceClass = serviceBean.getClass();
			String methodName = xxlRpcRequest.getMethodName();
			Class<?>[] parameterTypes = xxlRpcRequest.getParameterTypes();
			Object[] parameters = xxlRpcRequest.getParameters();

            // do invoke
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
			Object result = method.invoke(serviceBean, parameters);

            // write result
			xxlRpcResponse.setResult(result);
		} catch (Throwable t) {
			// catch error
			logger.error(">>>>>>>>>>> xxl-rpc provider invokeService error.", t);

			String result = ThrowableTool.toString(t);
			xxlRpcResponse.setErrorMsg( result.length()>1000?result.substring(0, 1000)+"...":result );
		}

		return xxlRpcResponse;
	}

}
