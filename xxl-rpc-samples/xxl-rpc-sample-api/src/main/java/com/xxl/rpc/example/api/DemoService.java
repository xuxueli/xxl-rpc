package com.xxl.rpc.example.api;

import com.xxl.rpc.example.api.dto.UserDTO;

/**
 * Demo API
 */
public interface DemoService {

	public UserDTO sayHi(String name);

}
