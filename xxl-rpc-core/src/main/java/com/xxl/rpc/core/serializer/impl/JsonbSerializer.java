package com.xxl.rpc.core.serializer.impl;

import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.Filter;
import com.xxl.rpc.core.serializer.Serializer;

/**
 * jsonb serializer
 *
 * @author xuxueli 2024-12-27
 */
public class JsonbSerializer extends Serializer {

    /**
     * jsonb writer features
     */
    public static JSONWriter.Feature[] writerFeatures = new JSONWriter.Feature[]{

    };

    /**
     * jsonb reader autoTypeBeforeHandler
     */
    private static final Filter autoTypeBeforeHandler = JSONReader.autoTypeFilter("com", "org","io");


    @Override
    public <T> byte[] serialize(T obj) {
        return JSONB.toBytes(obj,
                JSONWriter.Feature.WriteClassName);
    }

    @Override
    public <T> Object deserialize(byte[] bytes, Class<T> clazz) {
        return JSONB.parseObject(bytes, clazz,
                autoTypeBeforeHandler,
                JSONReader.Feature.SupportClassForName
                /*JSONReader.Feature.SupportAutoType*/);
    }

}
