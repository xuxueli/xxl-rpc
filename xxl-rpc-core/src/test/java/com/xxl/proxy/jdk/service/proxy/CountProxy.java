package com.xxl.proxy.jdk.service.proxy;

import com.xxl.proxy.jdk.service.ICount;
import com.xxl.proxy.jdk.service.impl.CountImpl;

/**
 * 这是一个代理类（增强CountImpl实现类）
 * 
 * @author Administrator
 * 
 */
public class CountProxy implements ICount {
	private CountImpl countImpl;

	/**
	 * 覆盖默认构造器
	 * 
	 * @param countImpl
	 */
	public CountProxy(CountImpl countImpl) {
		this.countImpl = countImpl;
	}

	@Override
	public void queryCount() {
		System.out.println("事务处理之前");
		// 调用委托类的方法;
		countImpl.queryCount();
		System.out.println("事务处理之后");
	}

	@Override
	public void updateCount() {
		System.out.println("事务处理之前");
		// 调用委托类的方法;
		countImpl.updateCount();
		System.out.println("事务处理之后");

	}

}