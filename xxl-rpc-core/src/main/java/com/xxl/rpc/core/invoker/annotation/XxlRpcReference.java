package com.xxl.rpc.core.invoker.annotation;

import com.xxl.rpc.core.invoker.call.CallType;
import com.xxl.rpc.core.invoker.route.LoadBalance;
import com.xxl.rpc.core.remoting.Client;
import com.xxl.rpc.core.remoting.impl.netty.client.NettyClient;
import com.xxl.rpc.core.serializer.Serializer;
import com.xxl.rpc.core.serializer.impl.JsonbSerializer;

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
     * client
     *
     * @return
     */
    Class<? extends Client> client() default NettyClient.class;

    /**
     * serializer
     *
     * @return
     */
    Class<? extends Serializer> serializer() default JsonbSerializer.class;

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
