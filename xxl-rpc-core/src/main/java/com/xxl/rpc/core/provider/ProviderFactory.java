package com.xxl.rpc.core.provider;

import com.xxl.rpc.core.boot.XxlRpcBootstrap;
import com.xxl.rpc.core.register.entity.RegisterInstance;
import com.xxl.rpc.core.remoting.Server;
import com.xxl.rpc.core.remoting.entity.XxlRpcRequest;
import com.xxl.rpc.core.remoting.entity.XxlRpcResponse;
import com.xxl.rpc.core.serializer.Serializer;
import com.xxl.rpc.core.util.IpUtil;
import com.xxl.rpc.core.util.NetUtil;
import com.xxl.rpc.core.util.ThrowableUtil;
import com.xxl.rpc.core.util.XxlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
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
		String ip = IpUtil.getIp();
		if (rpcBootstrap.getProviderConfig().getPort() <= 0) {
			rpcBootstrap.getProviderConfig().setPort(7080);
		}
		if (rpcBootstrap.getProviderConfig().getAddress()==null || rpcBootstrap.getProviderConfig().getAddress().isEmpty()) {
			String address = IpUtil.getIpPort(ip, rpcBootstrap.getProviderConfig().getPort());
			rpcBootstrap.getProviderConfig().setAddress(address);
		}
		if (NetUtil.isPortUsed(rpcBootstrap.getProviderConfig().getPort())) {
			throw new XxlRpcException("xxl-rpc provider port["+ rpcBootstrap.getProviderConfig().getPort() +"] is used.");
		}

		// 1、serializer init
		this.serializerInstance = rpcBootstrap.getProviderConfig().getSerializer().newInstance();

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
	 * @param xxlRpcRequest
	 * @return
	 */
	public XxlRpcResponse invokeService(XxlRpcRequest xxlRpcRequest) {

		//  make response
		XxlRpcResponse xxlRpcResponse = new XxlRpcResponse();
		xxlRpcResponse.setRequestId(xxlRpcRequest.getRequestId());

		// match service bean
		String serviceKey = makeServiceKey(xxlRpcRequest.getClassName(), xxlRpcRequest.getVersion());
		Object serviceBean = serviceInstanceStore.get(serviceKey);

		// valid
		if (serviceBean == null) {
			xxlRpcResponse.setErrorMsg("The serviceKey["+ serviceKey +"] not found.");
			return xxlRpcResponse;
		}

		// accessToken
		/*if (System.currentTimeMillis() - xxlRpcRequest.getCreateMillisTime() > 3*60*1000) {
			xxlRpcResponse.setErrorMsg("The timestamp difference between admin and executor exceeds the limit.");
			return xxlRpcResponse;
		}*/
		/*if (factory.getProviderConfig().getAccessToken()!=null
				&& !factory.getProviderConfig().getAccessToken().trim().isEmpty()
				&& !factory.getProviderConfig().getAccessToken().trim().equals(xxlRpcRequest.getAccessToken())) {
			xxlRpcResponse.setErrorMsg("The access token[" + xxlRpcRequest.getAccessToken() + "] is wrong.");
			return xxlRpcResponse;
		}*/

		try {
			// invoke
			Class<?> serviceClass = serviceBean.getClass();
			String methodName = xxlRpcRequest.getMethodName();
			Class<?>[] parameterTypes = xxlRpcRequest.getParameterTypes();
			Object[] parameters = xxlRpcRequest.getParameters();

            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
			Object result = method.invoke(serviceBean, parameters);

			/*FastClass serviceFastClass = FastClass.create(serviceClass);
			FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
			Object result = serviceFastMethod.invoke(serviceBean, parameters);*/

			xxlRpcResponse.setResult(result);
		} catch (Throwable t) {
			// catch error
			logger.error(">>>>>>>>>>> xxl-rpc provider invokeService error.", t);
			xxlRpcResponse.setErrorMsg(ThrowableUtil.toStringShort(t));
		}

		return xxlRpcResponse;
	}

}
