package com.xxl.rpc.demo.service.impl;

import org.springframework.stereotype.Service;

import com.xxl.rpc.demo.model.User;
import com.xxl.rpc.demo.service.IDemoService;
import com.xxl.rpc.netcom.common.annotation.SkeletonService;

@SkeletonService(stub=IDemoService.class)
@Service("demoService")
public class DemoServiceImpl implements IDemoService {
	
	@Override
	public User sayHi(String name) {
		User user = new User();
		user.setUserName(name);
		user.setWord("hi " + name + ", you are so beautiful.(" + System.currentTimeMillis() + ")");
		return user;
	}

}
