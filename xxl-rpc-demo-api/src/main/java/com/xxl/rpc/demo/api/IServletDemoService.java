package com.xxl.rpc.demo.api;

import com.xxl.rpc.demo.api.dto.UserDto;

/**
 * Servlet 方式 Demo
 */
public interface IServletDemoService {

	public UserDto sayHi(String name);

}
