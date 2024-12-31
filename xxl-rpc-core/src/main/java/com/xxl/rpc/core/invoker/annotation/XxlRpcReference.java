package com.xxl.rpc.core.invoker.annotation;

import com.xxl.rpc.core.invoker.call.CallType;
import com.xxl.rpc.core.invoker.route.LoadBalance;

import java.lang.annotation.*;

/**
 * rpc service annotation, skeleton of stub ("@Inherited" allow service use "Transactional")
 *
 * @author 2015-10-29 19:44:33
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XxlRpcReference {

    /**
     * appname
     *
     * @return
     */
    String appname();

    /**
     * version of this iface
     *
     * @return
     */
    String version() default "";

    /**
     * callType
     *
     * @return
     */
    CallType callType() default CallType.SYNC;

    /**
     * loadBalance
     *
     * @return
     */
    LoadBalance loadBalance() default LoadBalance.ROUND;

    /**
     * timeout
     *
     * @return
     */
    long timeout() default 1000;

}
