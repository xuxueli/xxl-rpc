package com.xxl.rpc.core.invoker.support;

import com.xxl.rpc.core.boot.support.SpringXxlRpcBootstrap;
import com.xxl.rpc.core.invoker.annotation.XxlRpcReference;
import com.xxl.rpc.core.invoker.reference.XxlRpcReferenceBean;
import com.xxl.rpc.core.provider.ProviderFactory;
import com.xxl.rpc.core.util.XxlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * xxl-rpc invoker factory, init service-registry and spring-bean by annotation (for spring)
 *
 * @author xuxueli 2018-10-19
 */
public class SpringInvokerFactory {
    private static Logger logger = LoggerFactory.getLogger(SpringInvokerFactory.class);

    public static boolean postProcessAfterInstantiation(final Object bean,
                                                        final String beanName,
                                                        final SpringXxlRpcBootstrap rpcBootstrap) throws BeansException {

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
                    referenceBean.setAppname(rpcReference.appname());
                    referenceBean.setIface(iface);
                    referenceBean.setVersion(rpcReference.version());
                    referenceBean.setCallType(rpcReference.callType());
                    referenceBean.setLoadBalance(rpcReference.loadBalance());
                    referenceBean.setTimeout(rpcReference.timeout());
                    //referenceBean.setAccessToken(rpcReference.accessToken());
                    referenceBean.setRpcBootstrap(rpcBootstrap);

                    // get proxyObj
                    Object serviceProxy = null;
                    try {
                        serviceProxy = referenceBean.getObject();
                    } catch (Throwable e) {
                        throw new XxlRpcException(e);
                    }

                    // set bean
                    field.setAccessible(true);
                    field.set(bean, serviceProxy);

                    logger.info(">>>>>>>>>>> xxl-rpc, invoker factory init reference bean success. remote appname = {}, serviceKey = {}, bean.field = {}.{}",
                            rpcReference.appname(), ProviderFactory.makeServiceKey(iface.getName(), rpcReference.version()), beanName, field.getName());

                    // collection
                    rpcBootstrap.addReferenceBean(referenceBean);
                }
            }
        });

        return true;
    }


}
