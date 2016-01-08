package com.xxl.rpc.demo.service;

import com.xxl.rpc.demo.model.User;

public interface IDemoService {
	public User sayHi(String name);
	
	/**
	 * 远程调用事务测试
	 * 	// Param建表语句
	 	CREATE TABLE `NewTable` (
		`key`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
		`value`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL 
		)
	 */
	public int updateParam(String key, String value);
	
	/**
	 * 服务注册，测试
	 * @param a
	 * @param b
	 * @return
	 */
	public int injectTest(int a, int b);
	
}
