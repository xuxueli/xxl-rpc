package com.xxl.rpc.sample.server.service;

import com.xxl.rpc.sample.api.DemoService;
import com.xxl.rpc.sample.api.dto.UserDTO;
import com.xxl.rpc.remoting.provider.annotation.XxlRpcService;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @author xuxueli
 */
@XxlRpcService
@Service
public class DemoServiceImpl implements DemoService {

	@Override
	public UserDTO sayHi(String name) {

		String word = MessageFormat.format("Hi {0}, from {1} as {2}",
				name, DemoServiceImpl.class.getName(), System.currentTimeMillis());

		if ("error".equalsIgnoreCase(name)) throw new RuntimeException("test exception.");

		return new UserDTO(name, word);
	}

}
