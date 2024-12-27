//package com.xxl.rpc.core.serializer.impl;
//
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.JSONObject;
//import com.alibaba.fastjson2.JSONReader;
//import com.alibaba.fastjson2.JSONWriter;
//import com.xxl.rpc.core.serializer.Serializer;
//
///**
// * json serializer
// *
// * @author xuxueli 2024-12-27
// */
//public class JsonSerializer extends Serializer {
//
//    @Override
//    public <T> byte[] serialize(T obj) {
//        return JSON.toJSONBytes(obj, JSONWriter.Feature.WriteClassName);
//    }
//
//    @Override
//    public <T> Object deserialize(byte[] bytes, Class<T> clazz) {
//        return JSON.parseObject(bytes, clazz, JSONReader.Feature.SupportClassForName);
//    }
//
//    @Override
//    public Object parseObject(Object obj, Class<?> clazz) {
//        if (obj == null) {
//            return null;
//        }
//        if (obj instanceof JSONObject) {
//            return ((JSONObject)obj).toJavaObject(clazz);
//        }
//        return obj;
//    }
//
//}
