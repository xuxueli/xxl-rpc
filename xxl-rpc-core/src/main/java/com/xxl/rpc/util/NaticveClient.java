package com.xxl.rpc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2018-11-25 00:35:22
 */
public class NaticveClient {
    private static Logger logger = LoggerFactory.getLogger(NaticveClient.class);


    /**
     * discovery
     *
     * @param adminAddressArr
     * @param biz
     * @param env
     * @param keys
     * @return
     */
    public static Map<String, List<String>> discovery(List<String> adminAddressArr, String biz, String env, Set<String> keys) {
        for (String adminAddressUrl: adminAddressArr) {

            // final url
            String url = adminAddressUrl + "/registry/discovery";
            url += "?biz=" + biz;
            url += "&env=" + env;
            for (String key:keys) {
                url += "&keys=" + key;
            }

            // get and valid
            Map<String, Object> respObj = getAndValid(url);

            // parse
            Map<String, List<String>> data = (Map<String, List<String>>) respObj.get("data");
            return data;
        }

        return null;
    }

    private static Map<String, Object> getAndValid(String url){
        // resp json
        String respJson = BaseHttpUtil.get(url);
        if (respJson == null) {
            return null;
        }

        // parse obj
        Map<String, Object> respObj = new BasicJsonParser().parseMap(respJson);
        int code = Integer.valueOf(String.valueOf(respObj.get("code")));
        if (code != 200) {
            logger.info("NaticveClient.discovery fail, msg={}", (respObj.containsKey("msg")?respObj.get("msg"):respJson) );
            return null;
        }
        return respObj;
    }

    /**
     * registry
     *
     * @param adminAddressArr
     * @param biz
     * @param env
     * @param keys
     * @param value
     * @return
     */
    public static boolean registry(List<String> adminAddressArr, String biz, String env, Set<String> keys, String value) {

        for (String adminAddressUrl: adminAddressArr) {

            // final url
            String url = adminAddressUrl + "/registry/registry";
            url += "?biz=" + biz;
            url += "&env=" + env;
            for (String key : keys) {
                url += "&keys=" + key;
            }
            url += "&value=" + value;

            // get and valid
            Map<String, Object> respObj = getAndValid(url);

            return respObj!=null?true:false;
        }
        return false;
    }

    /**
     * remove
     *
     * @param adminAddressArr
     * @param biz
     * @param env
     * @param keys
     * @param value
     * @return
     */
    public static boolean remove(List<String> adminAddressArr, String biz, String env, Set<String> keys, String value) {

        for (String adminAddressUrl: adminAddressArr) {

            // final url
            String url = adminAddressUrl + "/registry/remove";
            url += "?biz=" + biz;
            url += "&env=" + env;
            for (String key : keys) {
                url += "&keys=" + key;
            }
            url += "&value=" + value;

            // get and valid
            Map<String, Object> respObj = getAndValid(url);

            return respObj!=null?true:false;
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {

        System.out.println(discovery(Arrays.asList("http://localhost:8080/xxl-rpc-admin"), "xxl-rpc", "test", new HashSet<String>(Arrays.asList("service01"))));

        // registry
        System.out.println(registry(Arrays.asList("http://localhost:8080/xxl-rpc-admin"), "xxl-rpc", "test", new HashSet<String>(Arrays.asList("service01")), "127.0.0.1"));
        TimeUnit.SECONDS.sleep(2);
        System.out.println(discovery(Arrays.asList("http://localhost:8080/xxl-rpc-admin"), "xxl-rpc", "test", new HashSet<String>(Arrays.asList("service01"))));

        // remove
        System.out.println(remove(Arrays.asList("http://localhost:8080/xxl-rpc-admin"), "xxl-rpc", "test", new HashSet<String>(Arrays.asList("service01")), "127.0.0.1"));
        TimeUnit.SECONDS.sleep(2);
        System.out.println(discovery(Arrays.asList("http://localhost:8080/xxl-rpc-admin"), "xxl-rpc", "test", new HashSet<String>(Arrays.asList("service01"))));

    }

}
