package com.xxl.rpc.admin.service;


import com.xxl.rpc.admin.core.model.XxlRpcRegistry;
import com.xxl.rpc.admin.core.result.ReturnT;
import com.xxl.rpc.core.registry.impl.xxlrpcadmin.model.XxlRpcAdminRegistryRequest;
import com.xxl.rpc.core.registry.impl.xxlrpcadmin.model.XxlRpcAdminRegistryResponse;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;

/**
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface IXxlRpcRegistryService {

    // admin
    Map<String,Object> pageList(int start, int length, String env, String key);
    ReturnT<String> delete(int id);
    ReturnT<String> update(XxlRpcRegistry xxlRpcRegistry);
    ReturnT<String> add(XxlRpcRegistry xxlRpcRegistry);


    // ------------------------ remote registry ------------------------

    /**
     * refresh registry-value, check update and broacase
     */
    XxlRpcAdminRegistryResponse registry(XxlRpcAdminRegistryRequest registryRequest);

    /**
     * remove registry-value, check update and broacase
     */
    XxlRpcAdminRegistryResponse remove(XxlRpcAdminRegistryRequest registryRequest);

    /**
     * discovery registry-data, read file
     */
    XxlRpcAdminRegistryResponse discovery(XxlRpcAdminRegistryRequest registryRequest);

    /**
     * monitor update
     */
    DeferredResult<XxlRpcAdminRegistryResponse> monitor(XxlRpcAdminRegistryRequest registryRequest);

}
