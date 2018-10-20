package com.xxl.rpc.remoting.provider;

import com.xxl.rpc.registry.ServiceRegistry;
import com.xxl.rpc.remoting.net.NetEnum;
import com.xxl.rpc.remoting.net.Server;
import com.xxl.rpc.remoting.net.params.BaseCallback;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import com.xxl.rpc.serialize.Serializer;
import com.xxl.rpc.util.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * provider
 *
 * @author xuxueli 2015-10-31 22:54:27
 */
public class XxlRpcProviderFactory {
	private static final Logger logger = LoggerFactory.getLogger(XxlRpcProviderFactory.class);

	// ---------------------- config ----------------------

	private NetEnum netType;
	private Serializer serializer;

	private String ip;					// for registry
	private int port = 7080;			// default port
	private String accessToken;

	private Class<? extends ServiceRegistry> serviceRegistryClass;
	private Map<String, String> serviceRegistryParam;


	public XxlRpcProviderFactory() {
	}
	public void initConfig(NetEnum netType,
						  Serializer serializer,
						  String ip,
						  int port,
						  String accessToken,
						   Class<? extends ServiceRegistry> serviceRegistryClass,
						  Map<String, String> serviceRegistryParam) {

		this.netType = netType;
		this.serializer = serializer;
		this.ip = ip;
		this.port = port;
		this.accessToken = accessToken;
		this.serviceRegistryClass = serviceRegistryClass;
		this.serviceRegistryParam = serviceRegistryParam;
	}


	public Serializer getSerializer() {
		return serializer;
	}

	public int getPort() {
		return port;
	}


	// ---------------------- start / stop ----------------------

	private Server server;
	private ServiceRegistry serviceRegistry;

	public void start() throws Exception {
		// start server
		server = netType.serverClass.newInstance();
		server.setStartedCallback(new BaseCallback() {		// serviceRegistry started
			@Override
			public void run() throws Exception {
				// start registry
				if (serviceRegistryClass != null) {
					serviceRegistry = serviceRegistryClass.newInstance();
					serviceRegistry.start(serviceRegistryParam);

					if (serviceData.size() > 0) {
						String ipPort = IpUtil.getIpPort(ip, port);
						for (String serviceKey :serviceData.keySet()) {
							serviceRegistry.registry(serviceKey, ipPort);
						}
					}
				}
			}
		});
		server.setStopedCallback(new BaseCallback() {		// serviceRegistry stoped
			@Override
			public void run() {
				// stop registry
				if (serviceRegistry != null) {
					if (serviceData.size() > 0) {
						String ipPort = IpUtil.getIpPort(ip, port);
						for (String serviceKey :serviceData.keySet()) {
							serviceRegistry.remove(serviceKey, ipPort);
						}
					}
					serviceRegistry.stop();
				}
			}
		});
		server.start(this);
	}

	public void  stop() throws Exception {
		// stop server
		server.stop();
	}


	// ---------------------- server invoke ----------------------

	/**
	 * init local rpc service map
	 */
	private Map<String, Object> serviceData = new HashMap<String, Object>();

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
		serviceData.put(serviceKey, serviceBean);

		logger.info(">>>>>>>>>>> xxl-rpc, provider factory add service success. serviceKey = {}, serviceBean = {}", serviceKey, serviceBean.getClass());
	}

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
		Object serviceBean = serviceData.get(serviceKey);

		// valid
		if (serviceBean == null) {
			xxlRpcResponse.setResult(new RuntimeException("The ServiceBean["+ serviceKey +"] not found."));
			return xxlRpcResponse;
		}

		if (System.currentTimeMillis() - xxlRpcRequest.getCreateMillisTime() > 3*60*1000) {
			xxlRpcResponse.setResult(new RuntimeException("The timestamp difference between admin and executor exceeds the limit."));
			return xxlRpcResponse;
		}
		if (accessToken!=null && accessToken.trim().length()>0 && !accessToken.trim().equals(xxlRpcRequest.getAccessToken())) {
			xxlRpcResponse.setResult(new RuntimeException("The access token[" + xxlRpcRequest.getAccessToken() + "] is wrong."));
			return xxlRpcResponse;
		}

		// invoke
		try {
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
			t.printStackTrace();
			xxlRpcResponse.setError(t);
		}

		return xxlRpcResponse;
	}

}
