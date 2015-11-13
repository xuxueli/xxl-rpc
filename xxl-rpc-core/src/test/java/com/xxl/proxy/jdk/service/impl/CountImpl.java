package com.xxl.proxy.jdk.service.impl;

import com.xxl.proxy.jdk.service.ICount;

/**
 * 委托类(包含业务逻辑)
 * 
 * @author Administrator
 * 
 */
public class CountImpl implements ICount {

	@Override
	public void queryCount() {
		System.out.println("查看账户方法...");

	}

	@Override
	public void updateCount() {
		System.out.println("修改账户方法...");

	}

}
