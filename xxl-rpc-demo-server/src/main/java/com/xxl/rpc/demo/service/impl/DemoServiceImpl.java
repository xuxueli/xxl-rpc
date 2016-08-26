package com.xxl.rpc.demo.service.impl;

import com.xxl.rpc.demo.api.IDemoService;
import com.xxl.rpc.demo.api.dto.UserDto;
import com.xxl.rpc.netcom.common.annotation.XxlRpcService;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@XxlRpcService(IDemoService.class)
@Service
public class DemoServiceImpl implements IDemoService {

	@Override
	public UserDto sayHi(String name) {

		String word = MessageFormat.format("Hi {0}, from {1} as {2}",
				name, DemoServiceImpl.class.getName(), System.currentTimeMillis());

		return new UserDto(name, word);
	}

}
