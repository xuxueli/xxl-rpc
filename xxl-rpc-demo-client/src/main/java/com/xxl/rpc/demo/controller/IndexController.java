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
	
	// client端和server端，必须同为NIO
	@Resource
	private IDemoService demoServiceNetty;
	@Resource
	private IDemoService demoServiceMina;
	
	// client端和server点，必须同为jetty
	@Resource
	private IDemoService demoServiceJetty;
	
	// client端和server点，必须同为servlet
	@Resource
	private IDemoService demoServiceServlet;

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
		} else if (type == 3) {
			netCom = "jetty";
			user = demoServiceJetty.sayHi(userName);
		} else {
			netCom = "http";
			user = demoServiceServlet.sayHi(userName);
		}
		return netCom + ":" + user.getWord();
	}
	
}
