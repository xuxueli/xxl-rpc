package com.xxl.rpc.example.api;

import com.xxl.rpc.example.api.dto.UserDto;

/**
 * Demo API
 */
public interface IDemoService {

	public UserDto sayHi(String name);

}
