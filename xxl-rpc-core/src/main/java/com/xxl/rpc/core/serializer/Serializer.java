package com.xxl.rpc.core.serializer;

import java.util.List;

/**
 * serializer
 *
 * 		Tips：模板方法模式：定义一个操作中算法的骨架（或称为顶级逻辑），将一些步骤（或称为基本方法）的执行延迟到其子类中；
 * 		Tips：基本方法：抽象方法 + 具体方法final + 钩子方法；
 * 		Tips：Enum 时最好的单例方案；枚举单例会初始化全部实现，此处改为托管Class，避免无效的实例化；
 *
 * @author xuxueli 2015-10-30 21:02:55
 */
public abstract class Serializer {

	/**
	 * allow package list
	 *
	 * @param packageList
	 */
	public abstract void allowPackageList(List<String> packageList);

	/**
	 * serialize
	 *
	 * @param obj
	 * @return
	 * @param <T>
	 */
	public abstract <T> byte[] serialize(T obj);

	/**
	 * deserialize
	 *
	 * @param bytes
	 * @param clazz
	 * @return
	 * @param <T>
	 */
	public abstract <T> Object deserialize(byte[] bytes, Class<T> clazz);

}
