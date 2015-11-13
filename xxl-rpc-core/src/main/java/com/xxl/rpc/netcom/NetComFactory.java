package com.xxl.rpc.netcom;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.xxl.rpc.netcom.netty.annotation.RpcService;
import com.xxl.rpc.netcom.netty.server.NettyServer;
import com.xxl.rpc.serialize.Serializer;

/**
 * netcom init
 * @author xuxueli 2015-10-31 22:54:27
 *
 * <bean class="com.xxl.rpc.netcom.NetComFactory" />
 */
public class NetComFactory implements ApplicationContextAware, InitializingBean {
	private static final Logger logger = LoggerFactory.getLogger(NetComFactory.class);
	
	private int port = 9999;
	private String serialize;
	private boolean provider_switch = true;
	private boolean consumer_switch = true;
	private boolean zookeeper_switch = false;
	public String getSerialize() {
		return serialize;
	}
	public void setSerialize(String serialize) {
		this.serialize = serialize;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
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
	 * local rpc service map
	 */
	private Map<String, Object> serviceMap = new HashMap<String, Object>();
	private void initServiceMap(ApplicationContext applicationContext){
		// init local rpc service map
		Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
                serviceMap.put(interfaceName, serviceBean);
            }
        }
	} 
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (provider_switch) {
			// init rpc provider
			initProvider();
		}
		if (consumer_switch) {
			// init rpc comsumer
			initComsumer();
		}
	}
	
	/**
	 * init rpc provider
	 */
	private void initProvider() throws Exception {
		Serializer serializer = Serializer.getInstance(serialize);
		new NettyServer(serviceMap, serializer, port, zookeeper_switch).start();
		logger.info(">>>>>>>>>>> xxl-mq provider is running, serializer:{}, port:{}", serializer, port);
		
	}
	
	/**
	 * init rpc comsumer
	 */
	private void initComsumer(){
		
	}
}
