package com.xxl.rpc.demo.client.controller;

import com.xxl.rpc.demo.api.IJettyDemoService;
import com.xxl.rpc.demo.api.IMinaDemoService;
import com.xxl.rpc.demo.api.INettyDemoService;
import com.xxl.rpc.demo.api.IServletDemoService;
import org.springframework.stereotype.Controller;
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
	public List<String> http() throws Exception {
		String userName = "jack";

		List<String> list = new ArrayList<String>();
		list.add(nettyDemoService.sayHi(userName).toString());
		list.add(minaDemoService.sayHi(userName).toString());
		//list.add(jettyDemoService.sayHi(userName).toString());
		list.add(servletDemoService.sayHi(userName).toString());

		return list;
	}
	
}
