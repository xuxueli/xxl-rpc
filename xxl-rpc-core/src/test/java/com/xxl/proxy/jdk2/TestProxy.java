package com.xxl.proxy.jdk2;

import com.xxl.proxy.jdk2.proxy.BookFacadeProxy;
import com.xxl.proxy.jdk2.service.IBookFacade;
import com.xxl.proxy.jdk2.service.impl.BookFacadeImpl;

/**
 * 与静态代理类对照的是动态代理类，动态代理类的字节码在程序运行时由Java反射机制动态生成，无需程序员手工编写它的源代码。动态代理类不仅简化了编程工作，
 * 而且提高了软件系统的可扩展性，因为Java 反射机制可以生成任意类型的动态代理类。java.lang.reflect
 * 包中的Proxy类和InvocationHandler 接口提供了生成动态代理类的能力。
 */
public class TestProxy {

	public static void main(String[] args) {
		BookFacadeProxy proxy = new BookFacadeProxy();
		IBookFacade bookProxy = (IBookFacade) proxy.bind(new BookFacadeImpl());
		bookProxy.addBook();
	}

}