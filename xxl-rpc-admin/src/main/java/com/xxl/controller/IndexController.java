package com.xxl.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxl.controller.interceptor.PermessionLimit;
import com.xxl.controller.interceptor.PermissionInterceptor;
import com.xxl.core.result.ReturnT;

/**
 * Base 
 * @author xuxueli 2016-3-19 13:56:28
 */
@Controller
@RequestMapping("/")
public class IndexController {
	
	@RequestMapping("")
	@PermessionLimit(login=false)
	public String index(HttpServletRequest request){
		if (PermissionInterceptor.ifLogin(request)) {
			return "redirect:/rpc";
		} else {
			return "login";
		}
	}
	
	@RequestMapping("/login")
	@PermessionLimit(login=false)
	@ResponseBody
	public ReturnT<String> login(HttpServletResponse response, String userName, String password){
		boolean ret = PermissionInterceptor.login(response, userName, password);
		return ret?ReturnT.SUCCESS:new ReturnT<String>(500, "登录失败");
	}
	
	@RequestMapping("/logout")
	@PermessionLimit(login=false)
	@ResponseBody
	public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response){
		PermissionInterceptor.logout(request, response);
		return ReturnT.SUCCESS;
	}
	
}
