package com.xxl.proxy.cglib.service.impl;

/**
 * 这个是没有实现接口的实现类
 * 
 * Cglib动态代理 JDK的动态代理机制只能代理实现了接口的类，而不能实现接口的类就不能实现JDK的动态代理，cglib是针对类来实现代理的，
 * 他的原理是对指定的目标类生成一个子类，并覆盖其中方法实现增强，但因为采用的是继承，所以不能对final修饰的类进行代理。
 */
public class BookFacadeImpl {
	public void addBook() {
		System.out.println("增加图书的普通方法...");
	}
}