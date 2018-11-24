package com.xxl.rpc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author xuxueli 2018-11-25 00:55:31
 */
public class BaseHttpUtil {
    private static Logger logger = LoggerFactory.getLogger(NaticveClient.class);

    public static String get(String url) {
        BufferedReader bufferedReader = null;
        try {
            URL realUrl = new URL(url);

            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            //connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();

            //Map<String, List<String>> map = connection.getHeaderFields();
            int statusCode = ((HttpURLConnection) connection).getResponseCode();
            if (statusCode == 200) {
                bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String result = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                return result;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e2) {
                logger.error(e2.getMessage(), e2);
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String url = "http://localhost:8080/xxl-rpc-admin/registry/discovery?biz=xxl-rpc&env=test&key=service01";
        System.out.println(get(url));
    }

}
