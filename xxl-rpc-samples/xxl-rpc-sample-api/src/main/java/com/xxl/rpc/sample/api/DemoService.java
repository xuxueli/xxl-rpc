package com.xxl.rpc.sample.api;

import com.xxl.rpc.sample.api.dto.UserDTO;

/**
 * Demo API
 */
public interface DemoService {

	public UserDTO sayHi(String name);

}
