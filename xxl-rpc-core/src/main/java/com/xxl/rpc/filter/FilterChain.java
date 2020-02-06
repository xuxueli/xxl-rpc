package com.xxl.rpc.filter;

import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;

import java.util.List;

/**
 * @author weizibin
 * @since 2020/2/4 下午5:40
 */
public class FilterChain {

    private final int index;

    private final List<Filter> filters;
    private final Delegate delegate;

    public FilterChain(List<Filter> filters, Delegate delegate) {
        this.filters = filters;
        this.delegate = delegate;
        this.index = 0;
    }

    private FilterChain(FilterChain parent, int index) {
        this.filters = parent.getFilters();
        this.delegate = parent.getDelegate();
        this.index = index;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public Delegate getDelegate() {
        return delegate;
    }

    public XxlRpcResponse doFilter(XxlRpcRequest request) throws Exception {
        if (this.index < filters.size()) {
            Filter filter = filters.get(this.index);
            FilterChain chain = new FilterChain(this, this.index + 1);
            return filter.doFilter(request, chain);
        } else {
            return delegate.doInvoke(request);
        }
    }

    public interface Delegate {
        XxlRpcResponse doInvoke(XxlRpcRequest request) throws Exception;
    }
}
