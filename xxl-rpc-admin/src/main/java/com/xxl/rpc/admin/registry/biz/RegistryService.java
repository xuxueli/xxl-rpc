package com.xxl.rpc.admin.registry.biz;

import com.xxl.rpc.admin.registry.model.*;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * @author xuxueli 2018-12-03
 */
public interface RegistryService {

    /**
     * register
     *
     * logic：
     *      1、async run -> write db + broadcast message -> refresh cache + push client
     *      2、single-client register single-app
     *
     * @param request   client instance
     * @return
     */
    OpenApiResponse register(RegisterRequest request);

    /**
     * unregister
     *
     * @param request
     * @return
     */
    OpenApiResponse unregister(RegisterRequest request);

    /**
     * discovery
     *
     * logic：
     *      1、only read cache
     *      2、
     */
    DiscoveryResponse discovery(DiscoveryRequest request);

    /**
     * monitor
     *
     * logic：
     *      1、support client monitor，long-polling
     *      2、push client when registry changed
     */
    DeferredResult<OpenApiResponse> monitor(DiscoveryRequest request);

}
