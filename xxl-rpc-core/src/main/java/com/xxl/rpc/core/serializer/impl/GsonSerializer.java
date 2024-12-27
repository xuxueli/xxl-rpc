//package com.xxl.rpc.core.serializer.impl;
//
//import com.xxl.rpc.core.serializer.Serializer;
//import com.xxl.rpc.core.util.GsonTool;
//import com.xxl.rpc.core.util.XxlRpcException;
//
///**
// * <!-- gson -->
// * <dependency>
// * 	<groupId>com.google.code.gson</groupId>
// * 	<artifactId>gson</artifactId>
// * 	<version>${gson.version}</version>
// * </dependency>
// */
//public class GsonSerializer extends Serializer {
//
//    @Override
//    public <T> byte[] serialize(T obj) {
//        try {
//            return GsonTool.toJson(obj).getBytes();
//        } catch (Exception e) {
//            throw new XxlRpcException(e);
//        }
//    }
//
//    @Override
//    public <T> Object deserialize(byte[] bytes, Class<T> clazz) {
//        try {
//            return GsonTool.fromJson(new String(bytes), clazz);
//        } catch (Exception e) {
//            throw new XxlRpcException(e);
//        }
//    }
//
//}
