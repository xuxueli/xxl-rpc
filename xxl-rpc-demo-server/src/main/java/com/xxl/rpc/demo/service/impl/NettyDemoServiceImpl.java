package com.xxl.rpc.demo.service.impl;

import com.xxl.rpc.demo.api.INettyDemoService;
import com.xxl.rpc.demo.api.dto.UserDto;
import com.xxl.rpc.netcom.common.annotation.XxlRpcService;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@XxlRpcService(INettyDemoService.class)
@Service("nettyDemoService")
public class NettyDemoServiceImpl implements INettyDemoService {

	@Override
	public UserDto sayHi(String name) {

		String word = MessageFormat.format("Hi {0}, from {1} as {2}",
				name, NettyDemoServiceImpl.class.getName(), System.currentTimeMillis());

		return new UserDto(name, word);
	}

}
