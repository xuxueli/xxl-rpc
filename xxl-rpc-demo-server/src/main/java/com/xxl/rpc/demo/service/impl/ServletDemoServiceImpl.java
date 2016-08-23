package com.xxl.rpc.demo.service.impl;

import com.xxl.rpc.demo.api.IServletDemoService;
import com.xxl.rpc.demo.api.dto.UserDto;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service("servletDemoService")
public class ServletDemoServiceImpl implements IServletDemoService {

	@Override
	public UserDto sayHi(String name) {

		String word = MessageFormat.format("Hi {0}, from {1} as {2}",
				name, ServletDemoServiceImpl.class.getName(), System.currentTimeMillis());

		return new UserDto(name, word);
	}

}
