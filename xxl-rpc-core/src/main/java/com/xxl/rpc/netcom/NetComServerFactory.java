package com.xxl.rpc.netcom;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.xxl.rpc.netcom.common.NetComEnum;
import com.xxl.rpc.netcom.common.annotation.SkeletonService;
import com.xxl.rpc.netcom.common.server.IServer;
import com.xxl.rpc.serialize.Serializer;

/**
 * netcom init
 * @author xuxueli 2015-10-31 22:54:27
 *
 * <bean class="com.xxl.rpc.netcom.NetComFactory" />
 */
public class NetComServerFactory implements ApplicationContextAware, InitializingBean {
	private static final Logger logger = LoggerFactory.getLogger(NetComServerFactory.class);
	
	private String netcom_type = NetComEnum.NETTY.name();
	private int port = 9999;
	private String serialize = Serializer.SerializeType.HESSIAN.name();
	private boolean provider_switch = true;
	private boolean consumer_switch = true;
	private boolean zookeeper_switch = false;
	public String getNetcom_type() {
		return netcom_type;
	}
	public void setNetcom_type(String netcom_type) {
		this.netcom_type = netcom_type;
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
	public boolean isProvider_switch() {
		return provider_switch;
	}
	public void setProvider_switch(boolean provider_switch) {
		this.provider_switch = provider_switch;
	}
	public boolean isConsumer_switch() {
		return consumer_switch;
	}
	public void setConsumer_switch(boolean consumer_switch) {
		this.consumer_switch = consumer_switch;
	}
	public boolean isZookeeper_switch() {
		return zookeeper_switch;
	}
	public void setZookeeper_switch(boolean zookeeper_switch) {
		this.zookeeper_switch = zookeeper_switch;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (provider_switch) {
			initServiceMap(applicationContext);
		}
	}
	
	/**
	 * init local rpc service map
	 */
	private Map<String, Object> serviceMap = new HashMap<String, Object>();
	private void initServiceMap(ApplicationContext applicationContext){
		Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(SkeletonService.class);
        if (serviceBeanMap!=null && serviceBeanMap.size()>0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                String interfaceName = serviceBean.getClass().getAnnotation(SkeletonService.class).stub().getName();
                serviceMap.put(interfaceName, serviceBean);
            }
        }
	} 
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (provider_switch) {
			// init rpc provider
			IServer server = IServer.getInstance(netcom_type, serviceMap, port, serialize, zookeeper_switch);
			server.start();
			logger.info(">>>>>>>>>>> xxl-mq provider is running, netcom_type:{}, port:{}, serialize:{}", netcom_type, port, serialize);
		}
		if (consumer_switch) {
			// init rpc comsumer
		}
	}
	
}
