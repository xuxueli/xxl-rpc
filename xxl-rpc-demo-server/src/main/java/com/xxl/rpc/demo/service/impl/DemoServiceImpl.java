package com.xxl.rpc.demo.service.impl;

import java.text.MessageFormat;

import org.springframework.stereotype.Service;

import com.xxl.rpc.demo.model.User;
import com.xxl.rpc.demo.service.IDemoService;
import com.xxl.rpc.netcom.common.annotation.RpcService;

@RpcService(IDemoService.class)
@Service("demoService")
public class DemoServiceImpl implements IDemoService {
	
	@Override
	public User sayHi(User user) {
		return new User(user.getUserName(), MessageFormat.format("{0} say:{1}", user.getUserName(), user.getWord()));
	}

}
