package com.xxl.proxy.cglib;

import com.xxl.proxy.cglib.proxy.BookFacadeCglib;
import com.xxl.proxy.cglib.service.impl.BookFacadeImpl;

public class TestCglib {

	public static void main(String[] args) {
		BookFacadeCglib cglib = new BookFacadeCglib();
		BookFacadeImpl bookCglib = (BookFacadeImpl) cglib.getInstance(new BookFacadeImpl());
		bookCglib.addBook();
	}
}