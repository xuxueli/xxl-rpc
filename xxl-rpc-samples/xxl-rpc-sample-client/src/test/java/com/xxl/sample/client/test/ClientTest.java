package com.xxl.sample.client.test;

import com.xxl.rpc.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.remoting.invoker.reference.XxlRpcReferenceBean;
import com.xxl.rpc.remoting.net.NetEnum;
import com.xxl.rpc.remoting.invoker.call.CallType;
import com.xxl.rpc.sample.api.DemoService;
import com.xxl.rpc.sample.api.dto.UserDTO;
import com.xxl.rpc.serialize.Serializer;

import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2018-10-21 20:48:40
 */
public class ClientTest {

	public static void main(String[] args) throws Exception {

		// init invoker factory
		XxlRpcInvokerFactory invokerFactory = new XxlRpcInvokerFactory();
		invokerFactory.start();



		// client test
		DemoService demoService = (DemoService) new XxlRpcReferenceBean(NetEnum.JETTY, Serializer.SerializeEnum.HESSIAN.getSerializer(),
                "127.0.0.1:7080", null, DemoService.class, null, 500, CallType.SYNC).getObject();

		demoService.sayHi("jack" );

		int count = 100;
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			UserDTO userDTO = demoService.sayHi("jack"+i );
			System.out.println(i + "##" + userDTO.toString());
		}
		long end = System.currentTimeMillis();
    	System.out.println("run count:"+ count +", cost:" + (end - start));



		TimeUnit.SECONDS.sleep(2);

		// stop invoker factory
		invokerFactory.stop();
	}

}
