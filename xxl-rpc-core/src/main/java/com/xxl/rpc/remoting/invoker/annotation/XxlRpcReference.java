package com.xxl.rpc.remoting.invoker.annotation;

import com.xxl.rpc.remoting.net.NetEnum;
import com.xxl.rpc.remoting.invoker.call.CallType;
import com.xxl.rpc.serialize.Serializer;

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

    NetEnum netType() default NetEnum.JETTY;
    Serializer.SerializeEnum serializer() default Serializer.SerializeEnum.HESSIAN;
    String address() default "";
    String accessToken() default "";

    //Class<?> iface;
    String version() default "";

    long timeout() default 1000;
    CallType callType() default CallType.SYNC;

}
