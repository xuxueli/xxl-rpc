package com.xxl.rpc.filter.impl;

import com.xxl.rpc.filter.Filter;
import com.xxl.rpc.filter.FilterChain;
import com.xxl.rpc.remoting.invoker.common.Invocation;
import com.xxl.rpc.remoting.invoker.generic.XxlRpcGenericService;
import com.xxl.rpc.remoting.invoker.reference.XxlRpcReferenceBean;
import com.xxl.rpc.util.ClassUtil;
import com.xxl.rpc.util.XxlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author weizibin
 * @since 2020/2/5 下午3:18
 */
public class MethodFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(XxlRpcReferenceBean.class);

    @Override
    public Object doFilter(Invocation invocation, FilterChain chain) throws Exception {
        String className = invocation.getClassName();
        String methodName = invocation.getMethodName();
        // filter method like "Object.toString()"
        if (className.equals(Object.class.getName())) {
            logger.info(">>>>>>>>>>> xxl-rpc proxy class-method not support [{}#{}]", className, methodName);
            throw new XxlRpcException("xxl-rpc proxy class-method not support");
        }
        return chain.doFilter(invocation);
    }
}
