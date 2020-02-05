package com.xxl.rpc.filter;

import com.xxl.rpc.remoting.invoker.common.Invocation;
import com.xxl.rpc.util.XxlRpcException;

/**
 * @author weizibin
 * @since 2020/2/4 下午2:55
 */
public interface Filter {

    Object doFilter(Invocation invocation, FilterChain chain) throws Exception;

}
