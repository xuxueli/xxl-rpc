package com.xxl.rpc.admin.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * Created by xuxueli on 16/8/28.
 */
@Component
public class XxlRpcRegistryImpl implements InitializingBean, DisposableBean {
    private final static Logger logger = LoggerFactory.getLogger(XxlRpcRegistryImpl.class);


    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }


}
