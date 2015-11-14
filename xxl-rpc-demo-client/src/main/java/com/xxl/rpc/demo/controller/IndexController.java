package com.xxl.rpc.demo.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxl.rpc.demo.model.User;
import com.xxl.rpc.demo.service.IDemoService;

@Controller
@RequestMapping("/")
public class IndexController {
	
	@Resource
	private IDemoService demoServiceHttp;
	@Resource
	private IDemoService demoServiceNetty;
	@Resource
	private IDemoService demoServiceMina;

	@RequestMapping("/{type}/{userName}/{word}")
	@ResponseBody
	public String http(@PathVariable int type, @PathVariable String userName, @PathVariable String word) throws Exception {
		User user = null;
		String netCom = null;
		if (type == 1) {
			netCom = "netty";
			user = demoServiceNetty.sayHi(userName);
		} else if (type == 2) {
			netCom = "mina";
			user = demoServiceMina.sayHi(userName);
		} else {
			netCom = "http";
			user = demoServiceHttp.sayHi(userName);
		}
		return netCom + ":" + user.getWord();
	}
	
}
