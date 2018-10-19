package com.xxl.rpc.remoting.invoker.impl;

import com.xxl.rpc.registry.ServiceRegistry;
import com.xxl.rpc.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.remoting.invoker.annotation.XxlRpcReference;
import com.xxl.rpc.remoting.invoker.reference.impl.XxlRpcSpringReferenceBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * xxl-rpc invoker factory (for spring)
 *
 * @author xuxueli 2018-10-19
 */
public class XxlRpcSpringInvokerFactory extends InstantiationAwareBeanPostProcessorAdapter implements InitializingBean,DisposableBean, BeanFactoryAware {


    // ---------------------- config ----------------------

    private String serviceRegistry;                                         // enum
    private Class<? extends ServiceRegistry> serviceRegistryClass;          // class.forname
    private Map<String, String> serviceRegistryParam;

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
        if (serviceRegistryClass == null) {
            if (serviceRegistry!=null && serviceRegistry.trim().length()>0) {
                ServiceRegistry.ServiceRegistryEnum serviceRegistryEnum = ServiceRegistry.ServiceRegistryEnum.match(serviceRegistry, null);
                if (serviceRegistryEnum !=null) {
                    serviceRegistryClass = serviceRegistryEnum.serviceRegistryClass;
                }
            }
        }


    }


    // ---------------------- util ----------------------


    private XxlRpcInvokerFactory xxlRpcInvokerFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.prepareConfig();

        // start invoker factory
        xxlRpcInvokerFactory = new XxlRpcInvokerFactory(serviceRegistryClass, serviceRegistryParam);
        xxlRpcInvokerFactory.start();
    }

    @Override
    public boolean postProcessAfterInstantiation(final Object bean, final String beanName) throws BeansException {

        ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                if (field.isAnnotationPresent(XxlRpcReference.class)) {
                    XxlRpcReference rpcReference = field.getAnnotation(XxlRpcReference.class);

                    // init reference bean
                    XxlRpcSpringReferenceBean referenceBean = new XxlRpcSpringReferenceBean();

                    referenceBean.setNetType(rpcReference.netType());
                    referenceBean.setSerialize(rpcReference.serialize());
                    referenceBean.setAddress(rpcReference.address());
                    referenceBean.setAccessToken(rpcReference.accessToken());
                    referenceBean.setIface(field.getDeclaringClass());
                    referenceBean.setVersion(rpcReference.version());
                    referenceBean.setTimeout(rpcReference.timeout());
                    referenceBean.setCallType(rpcReference.callType());


                    // set bean
                    field.setAccessible(true);
                    field.set(bean, referenceBean);
                }
            }
        });

        return super.postProcessAfterInstantiation(bean, beanName);
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
