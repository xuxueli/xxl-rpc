package com.xxl.proxy.jdk;

import com.xxl.proxy.jdk.service.impl.CountImpl;
import com.xxl.proxy.jdk.service.proxy.CountProxy;

/**
 * 代理模式 代理模式是常用的java设计模式，他的特征是代理类与委托类有同样的接口，代理类主要负责为委托类预处理消息、过滤消息、把消息转发给委托类，
 * 以及事后处理消息等
 * 。代理类与委托类之间通常会存在关联关系，一个代理类的对象与一个委托类的对象关联，代理类的对象本身并不真正实现服务，而是通过调用委托类的对象的相关方法
 * ，来提供特定的服务。 按照代理的创建时期，代理类可以分为两种。
 * 静态代理：由程序员创建或特定工具自动生成源代码，再对其编译。在程序运行前，代理类的.class文件就已经存在了。
 * 动态代理：在程序运行时，运用反射机制动态创建而成。
 * 
 */
public class TestCount {
	
	public static void main(String[] args) {
		CountImpl countImpl = new CountImpl();
		CountProxy countProxy = new CountProxy(countImpl);
		countProxy.updateCount();
		countProxy.queryCount();

	}
}