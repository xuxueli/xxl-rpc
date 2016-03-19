package com.xxl.controller.interceptor;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.xxl.core.util.CookieUtil;
import com.xxl.core.util.PropertiesUtil;

/**
 * 权限拦截, 简易版
 * @author xuxueli 2015-12-12 18:09:04
 */
public class PermissionInterceptor extends HandlerInterceptorAdapter {
	
	public static final String LOGIN_IDENTITY_KEY = "LOGIN_IDENTITY";
	
	public static boolean login(HttpServletResponse response, String admin, String password){
		if (admin!=null && password!=null) {
			Properties prop = PropertiesUtil.loadProperties("config.properties", true);
			String _admin = PropertiesUtil.getString(prop, "admin", null);
			String _password = PropertiesUtil.getString(prop, "password", null);
			if (admin.equals(_admin) && DigestUtils.md5Hex(password).equals(_password)) {
				CookieUtil.set(response, LOGIN_IDENTITY_KEY, DigestUtils.md5Hex(_password));
				return true;
			}
		}
		return false;
	}
	public static void logout(HttpServletRequest request, HttpServletResponse response){
		CookieUtil.remove(request, response, LOGIN_IDENTITY_KEY);
	}
	public static boolean ifLogin(HttpServletRequest request){
		String cookievalue = CookieUtil.getValue(request, LOGIN_IDENTITY_KEY);
		Properties prop = PropertiesUtil.loadProperties("config.properties", true);
		String _password = PropertiesUtil.getString(prop, "password", null);
		if (cookievalue!=null && cookievalue.equals(DigestUtils.md5Hex(_password))) {
			return true;
		}
		return false;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		if (!(handler instanceof HandlerMethod)) {
			return super.preHandle(request, response, handler);
		}
		
		HandlerMethod method = (HandlerMethod)handler;
		PermessionLimit permission = method.getMethodAnnotation(PermessionLimit.class);
		if (permission == null || (permission.login() && !ifLogin(request))) {
			response.sendRedirect(request.getContextPath() + "/");
		}
		
		return super.preHandle(request, response, handler);
	}
	
}
