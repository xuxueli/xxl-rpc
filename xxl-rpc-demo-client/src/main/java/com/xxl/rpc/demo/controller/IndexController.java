package com.xxl.rpc.demo.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxl.rpc.demo.model.User;
import com.xxl.rpc.demo.service.IDemoService;

@Controller
@RequestMapping("/")
public class IndexController {
	
	@Resource
	private IDemoService demoServiceHttp;

	@RequestMapping("/http")
	@ResponseBody
	public String http(String userName, String word) throws Exception {
		User user = demoServiceHttp.sayHi(new User(userName, word));
		return user.getWord().concat("-"+System.currentTimeMillis());
	}
	
	@Resource
	private IDemoService demoServiceNetty;
	
	@RequestMapping("/netty")
	@ResponseBody
	public String netty(String userName, String word) throws Exception {
		User user = demoServiceNetty.sayHi(new User(userName, word));
		return user.getWord().concat("-"+System.currentTimeMillis());
	}
}
