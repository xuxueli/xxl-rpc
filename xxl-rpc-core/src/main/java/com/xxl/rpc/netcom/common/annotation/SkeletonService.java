package com.xxl.rpc.netcom.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * rpc service annotation
 * ("@Inherited" allow service use "Transactional") 
 * @author 2015-10-29 19:44:33
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SkeletonService {

    Class<?> stub();
}
