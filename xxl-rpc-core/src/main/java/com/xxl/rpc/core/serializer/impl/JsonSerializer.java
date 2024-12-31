package com.xxl.rpc.core.serializer.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.Filter;
import com.xxl.rpc.core.serializer.Serializer;

import java.util.List;

/**
 * jsonb serializer
 *
 * @author xuxueli 2024-12-27
 */
public class JsonSerializer extends Serializer {

    /**
     * jsonb reader autoTypeBeforeHandler
     */
    private static Filter autoTypeBeforeHandler = JSONReader.autoTypeFilter("com", "org","io");

    /**
     * allowPackageList
     *
     * @param packageList
     */
    @Override
    public void allowPackageList(List<String> packageList) {
        if (packageList!=null && !packageList.isEmpty()) {
            autoTypeBeforeHandler = JSONReader.autoTypeFilter((String[]) packageList.toArray());
        }
    }

    @Override
    public <T> byte[] serialize(T obj) {
        return JSON.toJSONBytes(obj,
                JSONWriter.Feature.WriteClassName);
    }

    @Override
    public <T> Object deserialize(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(bytes, clazz,
                autoTypeBeforeHandler,
                JSONReader.Feature.SupportClassForName
                /*JSONReader.Feature.SupportAutoType*/);
    }

}
