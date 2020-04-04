//package com.xxl.rpc.core.util;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.reflect.TypeToken;
//
//import java.util.List;
//
///**
// * @author xuxueli 2020-04-05 03:42:00
// *
// *      <!-- gson -->
// * 		<dependency>
// * 			<groupId>com.google.code.gson</groupId>
// * 			<artifactId>gson</artifactId>
// * 			<version>${gson.version}</version>
// * 		</dependency>
// * 		<gson.version>2.8.6</gson.version>
// */
//public class GsonUtil {
//
//    private static Gson gson = new GsonBuilder()
//            .setDateFormat("yyyy-MM-dd HH:mm:ss")
//            .create();
//
//    /**
//     * 对象转json
//     *
//     * @param src
//     * @return String
//     */
//    public static String toJson(Object src) {
//        return gson.toJson(src);
//    }
//
//    /**
//     * json转特定Class对象
//     *
//     * @param json
//     * @param classOfT
//     * @return
//     */
//    public static <T> T fromJson(String json, Class<T> classOfT) {
//        T object = gson.fromJson(json, classOfT);
//        return object;
//    }
//
//    /**
//     * json转特定Class对象list
//     *
//     * @param json
//     * @param classOfT
//     * @return
//     */
//    public static <T> List<T> fromJsonList(String json, Class<T> classOfT) {
//        List<T> list = gson.fromJson(json, new TypeToken<List<T>>(){}.getType());
//        return list;
//    }
//
//}
