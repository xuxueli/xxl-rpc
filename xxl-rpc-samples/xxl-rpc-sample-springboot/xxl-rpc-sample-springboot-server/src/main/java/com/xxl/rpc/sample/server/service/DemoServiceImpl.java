package com.xxl.rpc.sample.server.service;

import com.xxl.rpc.sample.api.DemoService;
import com.xxl.rpc.sample.api.dto.UserDTO;
import com.xxl.rpc.core.provider.annotation.XxlRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author xuxueli
 */
@XxlRpcService
@Service
public class DemoServiceImpl implements DemoService {
	private static Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);

	@Override
	public UserDTO sayHi(String name) {

		// error
		if ("error".equalsIgnoreCase(name)) {
			throw new RuntimeException("test exception.");
		}

		// biz
		String word = MessageFormat.format("Hi {0}, this from {1} at {2}",
				name,
				DemoServiceImpl.class.getName(),
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		UserDTO userDTO = new UserDTO(name, word);

		//logger.info(userDTO.toString());
		return userDTO;
	}

}
