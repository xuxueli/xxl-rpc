package com.xxl.rpc.netcom;

import com.xxl.rpc.netcom.common.NetComEnum;
import com.xxl.rpc.netcom.common.annotation.XxlRpcService;
import com.xxl.rpc.netcom.common.codec.RpcRequest;
import com.xxl.rpc.netcom.common.codec.RpcResponse;
import com.xxl.rpc.netcom.common.server.IServer;
import com.xxl.rpc.registry.ZkServiceRegistry;
import com.xxl.rpc.serialize.Serializer;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * netcom init
 * @author xuxueli 2015-10-31 22:54:27
 *
 * <bean class="com.xxl.rpc.netcom.NetComFactory" />
 */
public class NetComServerFactory implements ApplicationContextAware, InitializingBean {
	private static final Logger logger = LoggerFactory.getLogger(NetComServerFactory.class);

	// ---------------------- server config ----------------------
	private int port = 7080;
	private int http_port = 7070;
	private NetComEnum netcom = NetComEnum.NETTY;
	private Serializer serializer = Serializer.SerializeEnum.HESSIAN.serializer;
	private boolean zookeeper_switch = false;

	public void setPort(int port) {
		this.port = port;
	}
	public void setHttp_port(int http_port) {
		this.http_port = http_port;
	}
	public void setNetcom(String netcom) {
		this.netcom = NetComEnum.autoMatch(netcom, NetComEnum.NETTY);
	}
	public void setSerializer(String serializer) {
		this.serializer = Serializer.SerializeEnum.match(serializer, Serializer.SerializeEnum.HESSIAN).serializer;
	}
	public Serializer getSerializer() {
		return serializer;
	}
	public void setZookeeper_switch(boolean zookeeper_switch) {
		this.zookeeper_switch = zookeeper_switch;
	}

	// ---------------------- server init ----------------------
	/**
	 * init local rpc service map
	 */
	private static Map<String, Object> serviceMap = new HashMap<String, Object>();
	public static RpcResponse invokeService(RpcRequest request, Object serviceBean) {
		if (serviceBean==null) {
			serviceBean = serviceMap.get(request.getClassName());
		}
		if (serviceBean == null) {
			// TODO
		}

		RpcResponse response = new RpcResponse();
		response.setRequestId(request.getRequestId());

		try {
			Class<?> serviceClass = serviceBean.getClass();
			String methodName = request.getMethodName();
			Class<?>[] parameterTypes = request.getParameterTypes();
			Object[] parameters = request.getParameters();

            /*Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(serviceBean, parameters);*/

			FastClass serviceFastClass = FastClass.create(serviceClass);
			FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);

			Object result = serviceFastMethod.invoke(serviceBean, parameters);

			response.setResult(result);
		} catch (Throwable t) {
			t.printStackTrace();
			response.setError(t);
		}

		return response;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(XxlRpcService.class);
		if (serviceBeanMap!=null && serviceBeanMap.size()>0) {
			for (Object serviceBean : serviceBeanMap.values()) {
				String interfaceName = serviceBean.getClass().getAnnotation(XxlRpcService.class).value().getName();
				serviceMap.put(interfaceName, serviceBean);
			}
		}

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// init rpc provider
		IServer server = netcom.serverClass.newInstance();
		server.start(port, serializer);

		// init rpc-http provider
		IServer httpserver = NetComEnum.JETTY.serverClass.newInstance();
		httpserver.start(http_port, serializer);

		if (zookeeper_switch) {
			ZkServiceRegistry.registerServices(port, serviceMap.keySet());
		}

	}
	
}
