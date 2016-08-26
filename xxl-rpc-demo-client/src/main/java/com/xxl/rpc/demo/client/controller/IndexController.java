package com.xxl.rpc.demo.client.controller;

import com.xxl.rpc.demo.api.IDemoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {
	
	@Resource
	private IDemoService demoService;


	@RequestMapping("")
	@ResponseBody
	public List<String> http() throws Exception {
		String userName = "jack";

		List<String> list = new ArrayList<String>();
		list.add(demoService.sayHi(userName).toString());

		return list;
	}
	
}
