package com.xxl.rpc.filter;

import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;

/**
 * @author weizibin
 * @since 2020/2/4 下午2:55
 */
public interface Filter {

    XxlRpcResponse doFilter(XxlRpcRequest request, FilterChain chain) throws Exception;

}
