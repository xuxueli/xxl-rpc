package com.xxl.rpc.demo.client.controller;

import com.xxl.rpc.demo.api.IJettyDemoService;
import com.xxl.rpc.demo.api.IMinaDemoService;
import com.xxl.rpc.demo.api.INettyDemoService;
import com.xxl.rpc.demo.api.IServletDemoService;
import com.xxl.rpc.demo.api.dto.UserDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {
	
	@Resource
	private INettyDemoService nettyDemoService;
	@Resource
	private IMinaDemoService minaDemoService;
	@Resource
	private IJettyDemoService jettyDemoService;
	@Resource
	private IServletDemoService servletDemoService;

	@RequestMapping("/demo")
	@ResponseBody
	public List<UserDto> http(@PathVariable int type, @PathVariable String userName, @PathVariable String word) throws Exception {

		List<UserDto> list = new ArrayList<UserDto>();
		list.add(nettyDemoService.sayHi(userName));
		list.add(minaDemoService.sayHi(userName));
		//list.add(jettyDemoService.sayHi(userName));
		list.add(servletDemoService.sayHi(userName));

		return list;
	}
	
}
