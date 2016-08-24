package com.xxl.rpc.netcom;

import com.xxl.rpc.netcom.common.NetComEnum;
import com.xxl.rpc.netcom.common.annotation.XxlRpcService;
import com.xxl.rpc.netcom.common.server.IServer;
import com.xxl.rpc.serialize.Serializer;
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
	
	private String netcom = NetComEnum.NETTY.name();
	private int port = 9999;
	private String serialize = Serializer.SerializeType.HESSIAN.name();
	private boolean zookeeper_switch = false;

	public String getNetcom() {
		return netcom;
	}
	public void setNetcom(String netcom) {
		this.netcom = netcom;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getSerialize() {
		return serialize;
	}
	public void setSerialize(String serialize) {
		this.serialize = serialize;
	}
	public boolean isZookeeper_switch() {
		return zookeeper_switch;
	}
	public void setZookeeper_switch(boolean zookeeper_switch) {
		this.zookeeper_switch = zookeeper_switch;
	}

	/**
	 * init local rpc service map
	 */
	private Map<String, Object> serviceMap = new HashMap<String, Object>();

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
		IServer server = IServer.getInstance(netcom, serviceMap, port, serialize, zookeeper_switch);
		server.start();
		logger.info(">>>>>>>>>>> xxl-rpc provider is running, netcom:{}, port:{}, serialize:{}", netcom, port, serialize);
	}
	
}
