package com.xxl.rpc.sample.server.service;

import com.xxl.rpc.sample.api.DemoService;
import com.xxl.rpc.sample.api.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * @author xuxueli
 */
public class DemoServiceImpl implements DemoService {
	private static final Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);

	@Override
	public UserDTO sayHi(String name) {

		String word = MessageFormat.format("Hi {0}, from {1} as {2}",
				name, DemoServiceImpl.class.getName(), String.valueOf(System.currentTimeMillis()));

		if ("error".equalsIgnoreCase(name)) {
			throw new RuntimeException("test exception.");
		}

		UserDTO userDTO = new UserDTO(name, word);
		logger.info(userDTO.toString());

		return userDTO;
	}

	@Override
	public UserDTO sayHi2(UserDTO userDTO) {
		UserDTO userDTO2 =new UserDTO("Jack", "Hi " + userDTO.getName());
		logger.info(userDTO2.toString());
		return userDTO2;
	}

}
