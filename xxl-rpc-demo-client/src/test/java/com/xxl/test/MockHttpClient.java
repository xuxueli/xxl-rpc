package com.xxl.test;

import com.xxl.rpc.demo.api.IDemoService;
import com.xxl.rpc.netcom.NetComClientProxy;
import com.xxl.rpc.netcom.common.NetComEnum;

/**
 * 客户端模拟, 四种RPC方案
 */
public class MockHttpClient {

	public static void main(String[] args) throws Exception {

		IDemoService httpService = (IDemoService) new NetComClientProxy("127.0.0.1:7080", NetComEnum.Plugin.JETTY.name(), "HESSIAN", IDemoService.class, 1000 * 5).getObject();
		System.out.println(httpService.sayHi("jack").toString());

	}

}
