package com.xxl.rpc.filter.impl;

import com.xxl.rpc.filter.ConsumerFilter;
import com.xxl.rpc.filter.FilterChain;
import com.xxl.rpc.remoting.invoker.generic.XxlRpcGenericService;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import com.xxl.rpc.util.ClassUtil;

/**
 * @author weizibin
 * @since 2020/2/5 下午3:18
 */
public class GenericFilter implements ConsumerFilter {

    @Override
    public XxlRpcResponse doFilter(XxlRpcRequest request, FilterChain chain) throws Exception {
        String className = request.getClassName();
        String methodName = request.getMethodName();
        Object[] args = request.getParameters();
        if (className.equals(XxlRpcGenericService.class.getName()) && methodName.equals("invoke")) {
            Class<?>[] paramTypes = null;
            if (args[3]!=null) {
                String[] paramTypes_str = (String[]) args[3];
                if (paramTypes_str.length > 0) {
                    paramTypes = new Class[paramTypes_str.length];
                    for (int i = 0; i < paramTypes_str.length; i++) {
                        paramTypes[i] = ClassUtil.resolveClass(paramTypes_str[i]);
                    }
                }
            }

            request.setClassName((String) args[0]);
            request.setVersion((String) args[1]);
            request.setMethodName((String) args[2]);
            request.setParameterTypes(paramTypes);
            request.setParameters((Object[]) args[4]);
        }
        return chain.doFilter(request);
    }
}
