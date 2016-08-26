package com.xxl.rpc.demo.api;

import com.xxl.rpc.demo.api.dto.UserDto;

/**
 * Mina 方式 Demo
 */
public interface IDemoService {

	public UserDto sayHi(String name);

}
