package com.xxl.rpc.core.serializer;

import com.xxl.rpc.core.serializer.impl.JsonbSerializer;
import com.xxl.rpc.core.util.XxlRpcException;

/**
 * serializer enum
 *
 * @author xuxueli 2015-10-30 21:02:55
 */
public enum SerializerEnum {

    JSONB(JsonbSerializer.class);

    // FASTJSON-JSONB、JDK、FST、Kryo、Protobuf、Thrift、Hession、Avro

    private Class<? extends Serializer> serializerClass;

    private SerializerEnum(Class<? extends Serializer> serializerClass) {
        this.serializerClass = serializerClass;
    }

    public Serializer getSerializer() {
        try {
            return serializerClass.newInstance();
        } catch (Exception e) {
            throw new XxlRpcException(e);
        }
    }

    public static SerializerEnum match(String name, SerializerEnum defaultSerializer) {
        for (SerializerEnum item : SerializerEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultSerializer;
    }

}