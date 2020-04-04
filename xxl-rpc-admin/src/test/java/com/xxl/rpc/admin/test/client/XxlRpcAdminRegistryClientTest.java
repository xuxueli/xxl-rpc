package com.xxl.rpc.admin.test.client;


import com.xxl.rpc.core.registry.impl.xxlrpcadmin.XxlRpcAdminRegistryClient;
import com.xxl.rpc.core.registry.impl.xxlrpcadmin.model.XxlRpcAdminRegistryDataParamVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class XxlRpcAdminRegistryClientTest {

    public static void main(String[] args) throws InterruptedException {
        XxlRpcAdminRegistryClient registryClient = new XxlRpcAdminRegistryClient("http://localhost:8080/xxl-rpc-admin/", null, "xxl-rpc", "test");

        // registry test
        List<XxlRpcAdminRegistryDataParamVO> registryDataList = new ArrayList<>();
        registryDataList.add(new XxlRpcAdminRegistryDataParamVO("service01", "address01"));
        registryDataList.add(new XxlRpcAdminRegistryDataParamVO("service02", "address02"));
        System.out.println("registry:" + registryClient.registry(registryDataList));
        TimeUnit.SECONDS.sleep(2);

        // discovery test
        Set<String> keys = new TreeSet<>();
        keys.add("service01");
        keys.add("service02");
        System.out.println("discovery:" + registryClient.discovery(keys));

        while (true) {
            TimeUnit.SECONDS.sleep(1);
        }

    }

}
