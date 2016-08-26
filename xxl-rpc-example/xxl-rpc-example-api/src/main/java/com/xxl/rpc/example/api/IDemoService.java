package com.xxl.rpc.example.api;

import com.xxl.rpc.example.api.dto.UserDto;

/**
 * Mina 方式 Demo
 */
public interface IDemoService {

	public UserDto sayHi(String name);

}
