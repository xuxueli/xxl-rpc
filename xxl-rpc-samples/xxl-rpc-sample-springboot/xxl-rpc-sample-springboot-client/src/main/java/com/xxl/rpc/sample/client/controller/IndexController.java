package com.xxl.rpc.sample.client.controller;

import com.xxl.rpc.sample.api.DemoService;
import com.xxl.rpc.sample.api.dto.UserDTO;
import com.xxl.rpc.core.invoker.annotation.XxlRpcReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {
	
	@XxlRpcReference(appname = "xxl-rpc-sample-springboot-server")
	private DemoService demoService;


	@RequestMapping("")
	@ResponseBody
	public String http(String name) {

		String result = "";
		result += ("<br> RPC Reqeuslt: name = " + name);

		try {
			UserDTO userDTO = demoService.sayHi(name);
			result += ("<br><br> RPC Response: " + userDTO);
		} catch (Exception e) {
			//e.printStackTrace();
			result += ("<br><br> RPC error: " + e.getMessage());
		}

		return result;
	}

}
