package com.xxl.rpc.demo.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xxl.rpc.demo.core.model.Param;
import com.xxl.rpc.demo.dao.IParamDao;
import com.xxl.rpc.demo.model.User;
import com.xxl.rpc.demo.service.IDemoService;
import com.xxl.rpc.demo.service.IInjectService;
import com.xxl.rpc.netcom.common.annotation.SkeletonService;

@SkeletonService(stub=IDemoService.class)
@Service("demoService")
public class DemoServiceImpl implements IDemoService {
	
	@Resource
	private IParamDao paramDao;
	@Autowired
	private IInjectService injectService;
	
	@Override
	public User sayHi(String name) {
		User user = new User();
		user.setUserName(name);
		user.setWord("hi " + name + ", you are so beautiful.(" + System.currentTimeMillis() + ")");
		return user;
	}

	@Transactional
	@Override
	public int updateParam(String key, String value) {
		Param param = paramDao.load(key);
		if (param==null) {
			param = new Param();
			param.setKey(key);
			param.setValue(value);
			return paramDao.save(param);
		} else {
			param.setValue(value);
			int ret = paramDao.update(param);
			ret = 3/0;
			return ret; 
		}
	}

	@Override
	public int injectTest(int a, int b) {
		return injectService.test(a, b);
	}

}
