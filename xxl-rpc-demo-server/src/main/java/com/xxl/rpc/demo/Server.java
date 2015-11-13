package com.xxl.rpc.demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Server{

	/**
	 * main 方式运行 spring-server (netty-rpc没问题, 但是http-rpc需要tomcat)
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		@SuppressWarnings({ "unused", "resource" })
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationcontext-*.xml");
	}
	
}
