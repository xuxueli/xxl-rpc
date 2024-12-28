package com.xxl.rpc.sample.client;

import com.xxl.rpc.core.boot.XxlRpcBootstrap;
import com.xxl.rpc.core.boot.config.BaseConfig;
import com.xxl.rpc.core.invoker.call.CallType;
import com.xxl.rpc.core.invoker.call.XxlRpcInvokeFuture;
import com.xxl.rpc.core.invoker.call.XxlRpcInvokeCallback;
import com.xxl.rpc.core.invoker.config.InvokerConfig;
import com.xxl.rpc.core.invoker.reference.XxlRpcReferenceBean;
import com.xxl.rpc.core.invoker.route.LoadBalance;
import com.xxl.rpc.core.register.impl.LocalRegister;
import com.xxl.rpc.core.register.entity.RegisterInstance;
import com.xxl.rpc.core.remoting.impl.netty.client.NettyClient;
import com.xxl.rpc.core.serializer.impl.JsonbSerializer;
import com.xxl.rpc.sample.api.DemoService;
import com.xxl.rpc.sample.api.dto.UserDTO;

import java.util.Collections;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2018-10-21 20:48:40
 */
public class XxlRpcClientAplication {

	public static void main(String[] args) throws Exception {

		// 1、LocalRegister
		LocalRegister localRegister = new LocalRegister();
		localRegister.register(new RegisterInstance("test", "server01", "127.0.0.1", 7080, null));

		// 2、XxlRpcBootstrap
		XxlRpcBootstrap rpcBootstrap = new XxlRpcBootstrap();
		rpcBootstrap.setBaseConfig(new BaseConfig("test", "client01"));
		rpcBootstrap.setRegister(localRegister);
		rpcBootstrap.setInvokerConfig(new InvokerConfig(true));

		// 3、start
		rpcBootstrap.start();

		// 4、XxlRpcReferenceBean build
		DemoService demoService_SYNC = buildReferenceBean(rpcBootstrap, CallType.SYNC);
		DemoService demoService_FUTURE = buildReferenceBean(rpcBootstrap, CallType.FUTURE);
		DemoService demoService_CALLBACK = buildReferenceBean(rpcBootstrap, CallType.CALLBACK);
		DemoService demoService_ONEWAY = buildReferenceBean(rpcBootstrap, CallType.ONEWAY);

		// 5、test rpc invoke
		testSYNC(demoService_SYNC);
		testFUTURE(demoService_FUTURE);
		testCALLBACK(demoService_CALLBACK);
		testONEWAY(demoService_ONEWAY);

		TimeUnit.SECONDS.sleep(5);

		// 6、stop
		rpcBootstrap.stop();
	}

	private static DemoService buildReferenceBean(XxlRpcBootstrap rpcBootstrap, CallType callType) throws Exception {
		XxlRpcReferenceBean referenceBean = new XxlRpcReferenceBean();
		referenceBean.setClient(NettyClient.class);
		referenceBean.setSerializer(JsonbSerializer.class);
		referenceBean.setCallType(callType);
		referenceBean.setLoadBalance(LoadBalance.ROUND);
		referenceBean.setIface(DemoService.class);
		referenceBean.setVersion(null);
		referenceBean.setTimeout(500);
		referenceBean.setAppname("server01");
		//referenceBean.setAddress("127.0.0.1:7080");
		//referenceBean.setAccessToken(null);
		referenceBean.setRpcBootstrap(rpcBootstrap);

		DemoService demoService = (DemoService) referenceBean.getObject();

		return demoService;
	}

	/**
	 * CallType.SYNC
	 */
	public static void testSYNC(DemoService demoService) throws Exception {

		// test
        //UserDTO userDTO = demoService.sayHi("[SYNC]jack");
		UserDTO userDTO = demoService.sayHi2(new UserDTO("[SYNC]jack", "hello"));
		System.out.println(userDTO);


		// test mult
		/*int count = 100;
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			UserDTO userDTO2 = demoService.sayHi("[SYNC]jack"+i );
			System.out.println(i + "##" + userDTO2.toString());
		}
		long end = System.currentTimeMillis();
    	System.out.println("run count:"+ count +", cost:" + (end - start));*/

	}


	/**
	 * CallType.FUTURE
	 */
	public static void testFUTURE(DemoService demoService) throws Exception {
		// test
		//demoService.sayHi("[FUTURE]jack" );
		demoService.sayHi2(new UserDTO("[FUTURE]jack", "hello"));
        Future<UserDTO> userDTOFuture = XxlRpcInvokeFuture.getFuture(UserDTO.class);
		UserDTO userDTO = userDTOFuture.get();

		System.out.println(userDTO.toString());
	}


	/**
	 * CallType.CALLBACK
	 */
	public static void testCALLBACK(DemoService demoService) throws Exception {
        // test
        XxlRpcInvokeCallback.setCallback(new XxlRpcInvokeCallback<UserDTO>() {
            @Override
            public void onSuccess(UserDTO result) {
                System.out.println(result);
            }

            @Override
            public void onFailure(Throwable exception) {
                exception.printStackTrace();
            }
        });

        //demoService.sayHi("[CALLBACK]jack");
		demoService.sayHi2(new UserDTO("[CALLBACK]jack", "hello"));
	}


	/**
	 * CallType.ONEWAY
	 */
	public static void testONEWAY(DemoService demoService) throws Exception {
		// test
        //demoService.sayHi("[ONEWAY]jack");
		demoService.sayHi2(new UserDTO("[ONEWAY]jack", "hello"));
	}

}
