package com.xxl.rpc.admin.registry;

import com.alibaba.fastjson2.JSON;
import com.xxl.rpc.admin.registry.model.*;
import com.xxl.tool.net.HttpTool;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class OpenApiControllerTest {
    private static final Logger logger = LoggerFactory.getLogger(OpenApiControllerTest.class);

    // admin-client
    private static String addressUrl = "http://127.0.0.1:8080/xxl-rpc-admin/openapi";


    @Test
    public void test_register() {
        RegisterRequest request = new RegisterRequest();
        request.setAccessToken("defaultaccesstoken");
        request.setEnv("test");
        request.setInstance(new RegisterInstance("app01", "127.0.0.1", 8080, "{}"));

        String responseBody = HttpTool.postBody(addressUrl + "/register",
                JSON.toJSONString(request),
                null,
                3000);
        logger.info(responseBody);
    }

    @Test
    public void test_unregister() {
        RegisterRequest request = new RegisterRequest();
        request.setAccessToken("defaultaccesstoken");
        request.setEnv("test");
        request.setInstance(new RegisterInstance("app01", "127.0.0.1", 8080, "{}"));

        String responseBody = HttpTool.postBody(addressUrl + "/unregister",
                JSON.toJSONString(request),
                null,
                3000);
        logger.info(responseBody);
    }

    @Test
    public void test_discovery() {

        DiscoveryRequest request = new DiscoveryRequest();
        request.setAccessToken("defaultaccesstoken");
        request.setEnv("test");
        request.setAppnameList(Arrays.asList("app01", "app02"));
        request.setSimpleQuery(false);

        String responseBody = HttpTool.postBody(addressUrl + "/discovery",
                JSON.toJSONString(request),
                null,
                3000);
        logger.info(responseBody);

    }

    @Test
    public void test_monitor() {

        DiscoveryRequest request = new DiscoveryRequest();
        request.setAccessToken("defaultaccesstoken");
        request.setEnv("test");
        request.setAppnameList(Arrays.asList("app01", "app02"));
        request.setSimpleQuery(false);

        String responseBody = HttpTool.postBody(addressUrl + "/monitor",
                JSON.toJSONString(request),
                null,
                3000);
        logger.info(responseBody);

    }


}
