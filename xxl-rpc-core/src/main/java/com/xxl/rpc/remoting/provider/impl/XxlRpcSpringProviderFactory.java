package com.xxl.rpc.remoting.provider.impl;

import com.xxl.rpc.registry.ServiceRegistry;
import com.xxl.rpc.remoting.net.NetEnum;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import com.xxl.rpc.remoting.provider.annotation.XxlRpcProvider;
import com.xxl.rpc.serialize.Serializer;
import com.xxl.rpc.util.IpUtil;
import com.xxl.rpc.util.NetUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * xxl-rpc provider (for spring)
 *
 * @author xuxueli 2018-10-18 18:09:20
 */
public class XxlRpcSpringProviderFactory extends XxlRpcProviderFactory implements ApplicationContextAware, InitializingBean,DisposableBean {

    // ---------------------- config ----------------------

    private String netType;
    private String serialize;

    private String ip;					        // for registry
    private int port;       			        // default port
    private String accessToken;

    private String serviceRegistry;                                         // enum
    private Class<? extends ServiceRegistry> serviceRegistryClass;          // class.forname
    private Map<String, String> serviceRegistryParam;


    // set
    public void setNetType(String netType) {
        this.netType = netType;
    }

    public void setSerialize(String serialize) {
        this.serialize = serialize;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setServiceRegistry(String serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setServiceRegistryClass(Class<? extends ServiceRegistry> serviceRegistryClass) {
        this.serviceRegistryClass = serviceRegistryClass;
    }

    public void setServiceRegistryParam(Map<String, String> serviceRegistryParam) {
        this.serviceRegistryParam = serviceRegistryParam;
    }


    // util
    private void prepareConfig(){

        // prepare config
        NetEnum netTypeEnum = NetEnum.autoMatch(netType, NetEnum.JETTY);
        Serializer serializer = Serializer.SerializeEnum.match(serialize, Serializer.SerializeEnum.HESSIAN).serializer;
        if (ip==null || ip.trim().length()==0) {
            ip = IpUtil.getIp();
        }
        if (port < 1) {
            port = 7080;
        }
        if (NetUtil.isPortUsed(port)) {
            throw new RuntimeException("xxl-rpc provider port["+ port +"] is used.");
        }

        if (serviceRegistryClass == null) {
            if (serviceRegistry!=null && serviceRegistry.trim().length()>0) {
                ServiceRegistry.ServiceRegistryEnum serviceRegistryEnum = ServiceRegistry.ServiceRegistryEnum.match(serviceRegistry, null);
                if (serviceRegistryEnum !=null) {
                    serviceRegistryClass = serviceRegistryEnum.serviceRegistryClass;
                }
            }
        }

        // init config
        super.initConfig(netTypeEnum, serializer, ip, port, accessToken, serviceRegistryClass, serviceRegistryParam);
    }


    // ---------------------- util ----------------------

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(XxlRpcProvider.class);
        if (serviceBeanMap!=null && serviceBeanMap.size()>0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                XxlRpcProvider xxlRpcProvider = serviceBean.getClass().getAnnotation(XxlRpcProvider.class);

                String iface = serviceBean.getClass().getName();
                String version = xxlRpcProvider.version();

                super.addService(iface, version, serviceBean);
            }
        }

        // TODO，addServices by api + prop

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.prepareConfig();
        super.start();
    }

    @Override
    public void destroy() throws Exception {
        super.stop();
    }

}
