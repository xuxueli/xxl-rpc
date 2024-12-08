package com.xxl.rpc.admin.service;

import com.xxl.rpc.admin.model.dto.RegistryRequest;
import com.xxl.rpc.admin.model.dto.RegistryResponse;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * @author xuxueli 2018-12-03
 */
public interface RegistryService {

    /**
     * refresh registry-value, check update and broacase
     */
    RegistryResponse registry(RegistryRequest registryRequest);

    /**
     * remove registry-value, check update and broacase
     */
    RegistryResponse remove(RegistryRequest registryRequest);

    /**
     * discovery registry-data, read file
     */
    RegistryResponse discovery(RegistryRequest registryRequest);

    /**
     * monitor update
     */
    DeferredResult<RegistryResponse> monitor(RegistryRequest registryRequest);


}
