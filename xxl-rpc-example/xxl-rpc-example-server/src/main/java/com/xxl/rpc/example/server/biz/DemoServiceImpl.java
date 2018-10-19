package com.xxl.rpc.example.server.biz;

import com.xxl.rpc.example.api.IDemoService;
import com.xxl.rpc.example.api.dto.UserDto;
import com.xxl.rpc.remoting.provider.annotation.XxlRpcProvider;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@XxlRpcProvider
@Service
public class DemoServiceImpl implements IDemoService {

	@Override
	public UserDto sayHi(String name) {

		String word = MessageFormat.format("Hi {0}, from {1} as {2}",
				name, DemoServiceImpl.class.getName(), System.currentTimeMillis());

		return new UserDto(name, word);
	}

}
