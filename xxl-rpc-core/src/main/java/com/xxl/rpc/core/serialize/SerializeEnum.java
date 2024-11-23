package com.xxl.rpc.core.serialize;

import com.xxl.rpc.core.serialize.impl.Hessian1Serializer;
import com.xxl.rpc.core.serialize.impl.HessianSerializer;
import com.xxl.rpc.core.util.XxlRpcException;

/**
 * serializer enum
 *
 * @author xuxueli 2015-10-30 21:02:55
 */
public enum SerializeEnum {

    HESSIAN(HessianSerializer.class),
    HESSIAN1(Hessian1Serializer.class);

    // JDK、FST、Kryo、Protobuf、Thrift、Hession、Avro

    private Class<? extends Serializer> serializerClass;

    private SerializeEnum(Class<? extends Serializer> serializerClass) {
        this.serializerClass = serializerClass;
    }

    public Serializer getSerializer() {
        try {
            return serializerClass.newInstance();
        } catch (Exception e) {
            throw new XxlRpcException(e);
        }
    }

    public static SerializeEnum match(String name, SerializeEnum defaultSerializer) {
        for (SerializeEnum item : SerializeEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultSerializer;
    }

}