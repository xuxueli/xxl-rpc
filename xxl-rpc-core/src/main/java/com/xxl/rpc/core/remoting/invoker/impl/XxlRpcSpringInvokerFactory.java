package com.xxl.rpc.core.remoting.invoker.impl;

import com.xxl.rpc.core.registry.Register;
import com.xxl.rpc.core.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.core.remoting.invoker.annotation.XxlRpcReference;
import com.xxl.rpc.core.remoting.invoker.reference.XxlRpcReferenceBean;
import com.xxl.rpc.core.remoting.provider.XxlRpcProviderFactory;
import com.xxl.rpc.core.util.XxlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * xxl-rpc invoker factory, init service-registry and spring-bean by annotation (for spring)
 *
 * @author xuxueli 2018-10-19
 */
public class XxlRpcSpringInvokerFactory implements InitializingBean, DisposableBean, BeanFactoryAware, InstantiationAwareBeanPostProcessor {
    private Logger logger = LoggerFactory.getLogger(XxlRpcSpringInvokerFactory.class);

    // ---------------------- config ----------------------

    private Class<? extends Register> serviceRegistryClass;          // class.forname
    private Map<String, String> serviceRegistryParam;


    public void setServiceRegistryClass(Class<? extends Register> serviceRegistryClass) {
        this.serviceRegistryClass = serviceRegistryClass;
    }

    public void setServiceRegistryParam(Map<String, String> serviceRegistryParam) {
        this.serviceRegistryParam = serviceRegistryParam;
    }


    // ---------------------- util ----------------------

    private XxlRpcInvokerFactory xxlRpcInvokerFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        // start invoker factory
        xxlRpcInvokerFactory = new XxlRpcInvokerFactory(serviceRegistryClass, serviceRegistryParam);
        xxlRpcInvokerFactory.start();
    }

    @Override
    public boolean postProcessAfterInstantiation(final Object bean, final String beanName) throws BeansException {

        // collection
        final Set<String> serviceKeyList = new HashSet<>();

        // parse XxlRpcReferenceBean
        ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                if (field.isAnnotationPresent(XxlRpcReference.class)) {
                    // valid
                    Class iface = field.getType();
                    if (!iface.isInterface()) {
                        throw new XxlRpcException("xxl-rpc, reference(XxlRpcReference) must be interface.");
                    }

                    XxlRpcReference rpcReference = field.getAnnotation(XxlRpcReference.class);

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


                    // get proxyObj
                    Object serviceProxy = null;
                    try {
                        serviceProxy = referenceBean.getObject();
                    } catch (Exception e) {
                        throw new XxlRpcException(e);
                    }

                    // set bean
                    field.setAccessible(true);
                    field.set(bean, serviceProxy);

                    logger.info(">>>>>>>>>>> xxl-rpc, invoker factory init reference bean success. serviceKey = {}, bean.field = {}.{}",
                            XxlRpcProviderFactory.makeServiceKey(iface.getName(), rpcReference.version()), beanName, field.getName());

                    // collection
                    String serviceKey = XxlRpcProviderFactory.makeServiceKey(iface.getName(), rpcReference.version());
                    serviceKeyList.add(serviceKey);

                }
            }
        });

        // mult discovery
        if (xxlRpcInvokerFactory.getRegister() != null) {
            try {
                xxlRpcInvokerFactory.getRegister().discovery(serviceKeyList);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return true;
    }


    @Override
    public void destroy() throws Exception {
        // stop invoker factory
        xxlRpcInvokerFactory.stop();
    }

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

}
