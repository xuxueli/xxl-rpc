package com.xxl.rpc.demo.dao.impl;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.xxl.rpc.demo.service.IDemoService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationcontext-*.xml")
public class ParamDaoImplTest {
	
	@Resource
	private IDemoService demoService;
	
	@Test
	public void updateTest() {
		demoService.updateParam("key", String.valueOf(System.currentTimeMillis()));
	}
	
}
