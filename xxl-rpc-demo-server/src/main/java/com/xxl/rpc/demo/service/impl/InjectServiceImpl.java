package com.xxl.rpc.demo.service.impl;

import org.springframework.stereotype.Service;

import com.xxl.rpc.demo.service.IInjectService;

@Service
public class InjectServiceImpl implements IInjectService {

	@Override
	public int test(int a, int b) {
		return a + b;
	}

}
