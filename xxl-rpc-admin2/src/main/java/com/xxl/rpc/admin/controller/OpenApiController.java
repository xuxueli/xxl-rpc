package com.xxl.rpc.admin.controller;

import com.xxl.rpc.admin.annotation.Permission;
import com.xxl.rpc.admin.model.dto.RegistryRequest;
import com.xxl.rpc.admin.model.dto.RegistryResponse;
import com.xxl.rpc.admin.service.RegistryService;
import com.xxl.tool.gson.GsonTool;
import com.xxl.tool.response.ResponseBuilder;
import com.xxl.tool.response.ResponseCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author xuxueli 2018-11-29
 */
@Controller
@RequestMapping("/api")
public class OpenApiController {

    @Resource
    private RegistryService registryService;

    @RequestMapping("/{uri}")
    @ResponseBody
    @Permission(login = false)
    public Object api(@PathVariable("uri") String uri, @RequestBody(required = false) String data){

        // valid
        if (uri==null || uri.trim().isEmpty()) {
            return new RegistryResponse(RegistryResponse.FAIL_CODE, "invalid request, uri-mapping empty.");
        }

        // services mapping
        if ("registry".equals(uri)) {
            /**
             * 服务注册 & 续约 API
             * 说明：新服务注册上线1s内广播通知接入方；需要接入方循环续约，否则服务将会过期（三倍于注册中心心跳时间）下线；
             */
            RegistryRequest registryRequest = (RegistryRequest) GsonTool.fromJson(data, RegistryRequest.class);
            return registryService.registry(registryRequest);
        } else if ("remove".equals(uri)) {
            /**
             * 服务摘除 API
             * 说明：新服务摘除下线1s内广播通知接入方；
             */
            RegistryRequest registryRequest = (RegistryRequest) GsonTool.fromJson(data, RegistryRequest.class);
            return registryService.remove(registryRequest);
        } else if ("discovery".equals(uri)) {
            /**
             * 服务发现 API
             * 说明：查询在线服务地址列表；
             */
            RegistryRequest registryRequest = (RegistryRequest) GsonTool.fromJson(data, RegistryRequest.class);
            return registryService.discovery(registryRequest);
        } else if ("monitor".equals(uri)) {
            /**
             * 服务监控 API
             * 说明：long-polling 接口，主动阻塞一段时间（三倍于注册中心心跳时间）；直至阻塞超时或服务注册信息变动时响应；
             */
            RegistryRequest registryRequest = (RegistryRequest) GsonTool.fromJson(data, RegistryRequest.class);
            return registryService.monitor(registryRequest);
        } else {
            return new RegistryResponse(RegistryResponse.FAIL_CODE, "invalid request, uri-mapping("+ uri +") not found.");
        }

    }

}
