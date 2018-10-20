package com.xxl.rpc.sample.client.controller;

import com.xxl.rpc.example.api.DemoService;
import com.xxl.rpc.example.api.dto.UserDTO;
import com.xxl.rpc.remoting.invoker.annotation.XxlRpcReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class IndexController {
	
	@XxlRpcReference
	private DemoService demoService;


	@RequestMapping("")
	@ResponseBody
	public UserDTO http(String name) {
		return demoService.sayHi(name);
	}

	
}
