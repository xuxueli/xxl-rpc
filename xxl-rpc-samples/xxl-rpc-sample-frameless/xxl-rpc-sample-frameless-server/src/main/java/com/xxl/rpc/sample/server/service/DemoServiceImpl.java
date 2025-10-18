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
		String word = MessageFormat.format("Hi {0}, this from {1} at {2}",
				name, DemoServiceImpl.class.getName(), String.valueOf(System.currentTimeMillis()));
		logger.info("response: {}", word);
		
		return new UserDTO(name, word);
	}

}
