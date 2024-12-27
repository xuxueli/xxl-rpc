//package com.xxl.rpc.core.test;
//
//import com.xxl.rpc.core.util.GsonTool;
//import org.junit.jupiter.api.Test;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class GsonToolTest {
//    //private static Logger logger = LoggerFactory.getLogger(GsonToolTest.class);
//
//    @Test
//    public void gsontest() {
//
//        Map<String, Object> map = new HashMap<>();
//        map.put("k1", 111);
//        map.put("k2", "222");
//
//        String json = GsonTool.toJson(map);
//        System.out.println(json);
//        //logger.info(json);
//
//        Map<String, Object> map2 = GsonTool.fromJson(json, Map.class);
//        System.out.println(GsonTool.toJson(map2));
//    }
//
//}
