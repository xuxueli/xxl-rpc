package com.xxl.rpc.core.test;

import com.xxl.rpc.core.serializer.Serializer;
import com.xxl.rpc.core.serializer.impl.JsonbSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xuxueli 2015-10-30 21:02:55
 */
public class SerializerTest {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        Serializer serializer = JsonbSerializer.class.newInstance();
        System.out.println(serializer);
        try {
            Map<String, String> map = new HashMap<String, String>();
            map.put("aaa", "111");
            map.put("bbb", "222");
            System.out.println(serializer.deserialize(serializer.serialize(map), Map.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
