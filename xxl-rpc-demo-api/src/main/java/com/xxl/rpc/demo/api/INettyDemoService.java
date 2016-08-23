package com.xxl.rpc.demo.api;

import com.xxl.rpc.demo.api.dto.UserDto;

/**
 * Netty 方式 Demo
 */
public interface INettyDemoService {

	public UserDto sayHi(String name);

}
