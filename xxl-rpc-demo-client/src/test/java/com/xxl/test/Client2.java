package com.xxl.test;

import com.xxl.rpc.demo.service.IDemoService;
import com.xxl.rpc.netcom.NetComClientProxy;
import com.xxl.rpc.netcom.common.NetComEnum;

public class Client2 {

	public static void main(String[] args) throws Exception {
    	IDemoService service2 = (IDemoService) new NetComClientProxy(NetComEnum.NETTY.name(), "127.0.0.1:9999", "HESSIAN", IDemoService.class, false, 1000 * 5).getObject();
    	service2.updateParam("key", String.valueOf(System.currentTimeMillis()));
	}

}
