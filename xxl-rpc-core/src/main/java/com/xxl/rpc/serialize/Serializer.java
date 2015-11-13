package com.xxl.rpc.serialize;

import java.util.HashMap;
import java.util.Map;

import com.xxl.rpc.serialize.impl.HessianSerializer;
import com.xxl.rpc.serialize.impl.JacksonSerializer;
import com.xxl.rpc.serialize.impl.ProtostuffSerializer;

/**
 * 序列化器
 * @author xuxueli 2015-10-30 21:02:55
 */
public abstract class Serializer {
	
	public abstract <T> byte[] serialize(T obj);
	public abstract <T> Object deserialize(byte[] bytes, Class<T> clazz);
	
	public enum SerializeType {
		HESSIAN, JSON, PROTOSTUFF;
	}
	
	private static Map<String, Serializer> serializerMap = new HashMap<String, Serializer>();
	static{
		serializerMap.put(SerializeType.HESSIAN.name(), new HessianSerializer());
		serializerMap.put(SerializeType.JSON.name(), new JacksonSerializer());
		serializerMap.put(SerializeType.PROTOSTUFF.name(), new ProtostuffSerializer());
	}
	
	public static Serializer getInstance(String serialize){
		if (serialize != null && serialize.trim().length() > 0) {
			Serializer serializer = serializerMap.get(serialize);
			if (serializer != null) {
				return serializer;
			}
		}
		return serializerMap.get(SerializeType.HESSIAN.name());
	}
	
	public static void main(String[] args) {
		Serializer serializer = Serializer.getInstance(null);
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
