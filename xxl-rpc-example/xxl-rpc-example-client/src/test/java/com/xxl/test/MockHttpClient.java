package com.xxl.test;

import com.xxl.rpc.example.api.IDemoService;
import com.xxl.rpc.netcom.NetComClientProxy;
import com.xxl.rpc.netcom.common.NetComEnum;
import com.xxl.rpc.serialize.Serializer;

/**
 * 客户端模拟, 四种RPC方案
 */
public class MockHttpClient {

	public static void main(String[] args) throws Exception {

		IDemoService httpService = (IDemoService) new NetComClientProxy("127.0.0.1:7070", NetComEnum.JETTY, Serializer.SerializeEnum.HESSIAN.serializer, IDemoService.class, 1000 * 5).getObject();
		System.out.println(httpService.sayHi("jack").toString());

	}

}
