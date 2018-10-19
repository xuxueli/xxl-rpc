package com.xxl.rpc.sample.server.impl;

import com.xxl.rpc.example.api.DemoService;
import com.xxl.rpc.example.api.dto.UserDTO;
import com.xxl.rpc.remoting.provider.annotation.XxlRpcProvider;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@XxlRpcProvider
@Service
public class DemoServiceImpl implements DemoService {

	@Override
	public UserDTO sayHi(String name) {

		String word = MessageFormat.format("Hi {0}, from {1} as {2}",
				name, DemoServiceImpl.class.getName(), System.currentTimeMillis());

		return new UserDTO(name, word);
	}

}
