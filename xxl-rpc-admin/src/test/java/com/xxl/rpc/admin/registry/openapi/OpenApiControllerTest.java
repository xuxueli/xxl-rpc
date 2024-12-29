package com.xxl.rpc.admin.registry.openapi;

import com.alibaba.fastjson2.JSON;
import com.xxl.rpc.admin.registry.model.*;
import com.xxl.tool.net.HttpTool;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.Arrays;

public class OpenApiControllerTest {
    private static final Logger logger = LoggerFactory.getLogger(OpenApiControllerTest.class);

    // admin-client
    private static String adminAddress = "http://127.0.0.1:8080/xxl-rpc-admin";


    @Test
    public void test_register() {

        // 1、register
        XxlRpcRegisterDTO.RegisterRequest request = new XxlRpcRegisterDTO.RegisterRequest();
        request.setAccessToken("defaultaccesstoken");
        request.setEnv("test");
        request.setInstance(new XxlRpcRegisterDTO.RegisterInstance("app01", "127.0.0.1", 8080, "{}"));

        String responseBody = HttpTool.postBody(adminAddress + "/openapi/register",
                JSON.toJSONString(request),
                null,
                3000);

        XxlRpcRegisterDTO.OpenApiResponse openApiResponse = JSON.parseObject(responseBody, XxlRpcRegisterDTO.OpenApiResponse.class);

        logger.info("result:{}, request:{}, responseBody:{}", openApiResponse.isSuccess()?"success":"fail", request, responseBody);
    }

    @Test
    public void test_unregister() {

        // 2、unregister
        XxlRpcRegisterDTO.RegisterRequest request = new XxlRpcRegisterDTO.RegisterRequest();
        request.setAccessToken("defaultaccesstoken");
        request.setEnv("test");
        request.setInstance(new XxlRpcRegisterDTO.RegisterInstance("app01", "127.0.0.1", 8080, "{}"));

        String responseBody = HttpTool.postBody(adminAddress + "/openapi/unregister",
                JSON.toJSONString(request),
                null,
                3000);

        XxlRpcRegisterDTO.OpenApiResponse openApiResponse = JSON.parseObject(responseBody, XxlRpcRegisterDTO.OpenApiResponse.class);
        logger.info("result:{}, request:{}, responseBody:{}", openApiResponse.isSuccess()?"success":"fail", request, responseBody);
    }

    @Test
    public void test_discovery() {

        // 3、discovery
        XxlRpcRegisterDTO.DiscoveryRequest request = new XxlRpcRegisterDTO.DiscoveryRequest();
        request.setAccessToken("defaultaccesstoken");
        request.setEnv("test");
        request.setAppnameList(Arrays.asList("app01", "app02"));
        request.setSimpleQuery(false);

        String responseBody = HttpTool.postBody(adminAddress + "/openapi/discovery",
                JSON.toJSONString(request),
                null,
                3000
        );
        XxlRpcRegisterDTO.DiscoveryResponse discoveryResponse = JSON.parseObject(responseBody, XxlRpcRegisterDTO.DiscoveryResponse.class);
        logger.info("result:{}, request:{}, responseBody:{}", discoveryResponse.isSuccess()?"success":"fail", request, responseBody);
    }

    @Test
    public void test_monitor() {

        // 4、monitor
        DiscoveryRequest request = new DiscoveryRequest();
        request.setAccessToken("defaultaccesstoken");
        request.setEnv("test");
        request.setAppnameList(Arrays.asList("app01", "app02"));
        request.setSimpleQuery(false);

        String responseBody = HttpTool.postBody(adminAddress + "/openapi/monitor",
                JSON.toJSONString(request),
                null,
                3000);

        DeferredResult<OpenApiResponse<String>> discoveryResponse = JSON.parseObject(responseBody, DeferredResult.class);
        OpenApiResponse<String> result = (OpenApiResponse<String>) discoveryResponse.getResult();
        logger.info("result:{}, request:{}, responseBody:{}", result.isSuccess()?"success":"fail", request, responseBody);
    }


}
