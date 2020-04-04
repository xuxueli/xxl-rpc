package com.xxl.rpc.admin.service;


import com.xxl.rpc.admin.core.model.XxlRpcRegistry;
import com.xxl.rpc.admin.core.model.XxlRpcRegistryData;
import com.xxl.rpc.admin.core.result.ReturnT;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.Map;

/**
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface IXxlRpcRegistryService {

    // admin
    Map<String,Object> pageList(int start, int length, String biz, String env, String key);
    ReturnT<String> delete(int id);
    ReturnT<String> update(XxlRpcRegistry xxlRpcRegistry);
    ReturnT<String> add(XxlRpcRegistry xxlRpcRegistry);


    // ------------------------ remote registry ------------------------

    /**
     * refresh registry-value, check update and broacase
     */
    ReturnT<String> registry(String accessToken, String biz, String env, List<XxlRpcRegistryData> registryDataList);

    /**
     * remove registry-value, check update and broacase
     */
    ReturnT<String> remove(String accessToken, String biz, String env, List<XxlRpcRegistryData> registryDataList);

    /**
     * discovery registry-data, read file
     */
    ReturnT<Map<String, List<String>>> discovery(String accessToken, String biz, String env, List<String> keys);

    /**
     * monitor update
     */
    DeferredResult<ReturnT<String>> monitor(String accessToken, String biz, String env, List<String> keys);

}
