package com.xxl.rpc.demo.api;

import com.xxl.rpc.demo.api.dto.UserDto;

/**
 * Mina 方式 Demo
 */
public interface IMinaDemoService {

	public UserDto sayHi(String name);

}
