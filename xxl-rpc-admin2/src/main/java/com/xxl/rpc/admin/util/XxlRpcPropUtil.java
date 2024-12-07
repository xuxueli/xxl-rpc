package com.xxl.rpc.admin.util;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * xxl-job config
 *
 * @author xuxueli 2017-04-28
 */

@Component
public class XxlRpcPropUtil implements InitializingBean {

    private static XxlRpcPropUtil single = null;
    public static XxlRpcPropUtil getSingle() {
        return single;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        single = this;
    }

    // conf
    @Value("${xxl.rpc.i18n}")
    private String i18n;

    public String getI18n() {
        if (!Arrays.asList("zh_CN", "zh_TC", "en").contains(i18n)) {
            return "zh_CN";
        }
        return i18n;
    }

}
