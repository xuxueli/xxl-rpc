package com.xxl.proxy.jdk2.service.impl;

import com.xxl.proxy.jdk2.service.IBookFacade;

public class BookFacadeImpl implements IBookFacade {

	@Override
	public void addBook() {
		System.out.println("增加图书方法。。。");
	}

}
