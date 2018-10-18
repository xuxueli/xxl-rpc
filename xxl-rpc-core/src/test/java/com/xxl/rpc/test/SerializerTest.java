package com.xxl.rpc.test;

import com.xxl.rpc.serialize.Serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xuxueli 2015-10-30 21:02:55
 */
public class SerializerTest {

    public static void main(String[] args) {
        Serializer serializer = Serializer.SerializeEnum.match("PROTOSTUFF", null).serializer;
        System.out.println(serializer);
        try {
            Map<String, String> map = new HashMap<String, String>();
            map.put("aaa", "111");
            map.put("bbb", "222");
            System.out.println(serializer.deserialize(serializer.serialize("ddddddd"), String.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
