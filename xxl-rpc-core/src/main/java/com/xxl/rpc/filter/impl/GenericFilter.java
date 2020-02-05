package com.xxl.rpc.filter.impl;

import com.xxl.rpc.filter.Filter;
import com.xxl.rpc.filter.FilterChain;
import com.xxl.rpc.remoting.invoker.common.Invocation;
import com.xxl.rpc.remoting.invoker.generic.XxlRpcGenericService;
import com.xxl.rpc.util.ClassUtil;

/**
 * @author weizibin
 * @since 2020/2/5 下午3:18
 */
public class GenericFilter implements Filter {
    @Override
    public Object doFilter(Invocation invocation, FilterChain chain) throws Exception {
        String className = invocation.getClassName();
        String methodName = invocation.getMethodName();
        Object[] args = invocation.getParameters();
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

            invocation.setClassName((String) args[0]);
            invocation.setVersion((String) args[1]);
            invocation.setMethodName((String) args[2]);
            invocation.setParameterTypes(paramTypes);
            invocation.setParameters((Object[]) args[4]);
        }
        return chain.doFilter(invocation);
    }
}
