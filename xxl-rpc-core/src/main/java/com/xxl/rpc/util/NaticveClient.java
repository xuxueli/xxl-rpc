package com.xxl.rpc.util;

import com.xxl.rpc.registry.impl.ZkServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * @author xuxueli 2018-11-25 00:35:22
 */
public class NaticveClient {
    private static Logger logger = LoggerFactory.getLogger(NaticveClient.class);


    public static Map<String, List<String>> discovery(String adminAddress, String biz, String env, Set<String> keys) {

        // admin address
        List<String> adminAddressUrls = new ArrayList<>();
        if (adminAddress.contains(",")) {
            adminAddressUrls.add(adminAddress);
        } else {
            adminAddressUrls.addAll(Arrays.asList(adminAddress.split(",")));
        }

        for (String adminAddressUrl: adminAddressUrls) {

            // final url
            String url = adminAddressUrl + "/registry/discovery";
            url += "?biz=" + biz;
            url += "&env=" + env;
            for (String key:keys) {
                url += "&keys=" + key;
            }

            // resp json
            String respJson = BaseHttpUtil.get(url);
            if (respJson == null) {
                continue;
            }

            // parse obj
            Map<String, Object> respObj = new BasicJsonParser().parseMap(respJson);
            int code = Integer.valueOf(String.valueOf(respObj.get("code")));
            if (code != 200) {
                logger.info("NaticveClient.discovery fail, msg={}", respObj.get("msg"));
            }
            Map<String, List<String>> data = (Map<String, List<String>>) respObj.get("data");
            return data;
        }

        return null;
    }

    public static void main(String[] args) {
        Map<String, List<String>> data = discovery("http://localhost:8080/xxl-rpc-admin", "xxl-rpc", "test", new HashSet<String>(Arrays.asList("service01")));
        System.out.println(data);
    }

}
