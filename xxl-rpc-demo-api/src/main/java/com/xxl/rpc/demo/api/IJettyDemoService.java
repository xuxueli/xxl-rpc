package com.xxl.rpc.demo.api;

import com.xxl.rpc.demo.api.dto.UserDto;

/**
 * Jetty 方式 Demo
 */
public interface IJettyDemoService {

	public UserDto sayHi(String name);

}
