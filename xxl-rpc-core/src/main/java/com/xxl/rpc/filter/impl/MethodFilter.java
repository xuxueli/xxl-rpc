package com.xxl.rpc.filter.impl;

import com.xxl.rpc.filter.ConsumerFilter;
import com.xxl.rpc.filter.FilterChain;
import com.xxl.rpc.remoting.invoker.reference.XxlRpcReferenceBean;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import com.xxl.rpc.util.XxlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author weizibin
 * @since 2020/2/5 下午3:18
 */
public class MethodFilter implements ConsumerFilter {

    private static final Logger logger = LoggerFactory.getLogger(XxlRpcReferenceBean.class);

    @Override
    public XxlRpcResponse doFilter(XxlRpcRequest request, FilterChain chain) throws Exception {
        String className = request.getClassName();
        String methodName = request.getMethodName();
        // filter method like "Object.toString()"
        if (className.equals(Object.class.getName())) {
            logger.info(">>>>>>>>>>> xxl-rpc proxy class-method not support [{}#{}]", className, methodName);
            throw new XxlRpcException("xxl-rpc proxy class-method not support");
        }
        return chain.doFilter(request);
    }
}
