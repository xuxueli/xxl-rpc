package com.xxl.rpc.sample.client;

import com.xxl.rpc.core.factory.XxlRpcFactory;
import com.xxl.rpc.core.factory.config.BaseConfig;
import com.xxl.rpc.core.invoker.call.CallType;
import com.xxl.rpc.core.invoker.call.XxlRpcInvokeCallback;
import com.xxl.rpc.core.invoker.call.XxlRpcInvokeFuture;
import com.xxl.rpc.core.invoker.config.InvokerConfig;
import com.xxl.rpc.core.invoker.reference.XxlRpcReferenceBean;
import com.xxl.rpc.core.invoker.route.LoadBalance;
import com.xxl.rpc.core.register.impl.LocalRegister;
import com.xxl.rpc.core.register.model.RegisterInstance;
import com.xxl.rpc.core.remoting.impl.netty.client.NettyClient;
import com.xxl.rpc.sample.api.DemoService;
import com.xxl.rpc.sample.api.dto.UserDTO;
import com.xxl.rpc.core.serializer.impl.HessianSerializer;

import java.util.Arrays;
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

		// factory
		XxlRpcFactory factory = new XxlRpcFactory();
		factory.setBaseConfig(new BaseConfig("test", "client01"));
		factory.setInvokerConfig(new InvokerConfig());
		factory.setRegister(new LocalRegister(new HashMap(){
			{
				RegisterInstance registerInstance = new RegisterInstance("test", "server01", "localhost", 7080, null);
				put("server01", new TreeSet<>(Collections.singletonList(registerInstance)));
			}
		}));

		factory.start();

		// test rpc
		testSYNC(factory);
		testFUTURE(factory);
		testCALLBACK(factory);
		testONEWAY(factory);

		TimeUnit.SECONDS.sleep(5);

		// stop
		factory.stop();
	}



	/**
	 * CallType.SYNC
	 */
	public static void testSYNC(XxlRpcFactory factory) throws Exception {
		// init client
		XxlRpcReferenceBean referenceBean = new XxlRpcReferenceBean();
		referenceBean.setClient(NettyClient.class);
		referenceBean.setSerializer(HessianSerializer.class);
		referenceBean.setCallType(CallType.SYNC);
		referenceBean.setLoadBalance(LoadBalance.ROUND);
		referenceBean.setIface(DemoService.class);
		referenceBean.setVersion(null);
		referenceBean.setTimeout(500);
		referenceBean.setAppname("server01");
		//referenceBean.setAddress("127.0.0.1:7080");
		//referenceBean.setAccessToken(null);

		DemoService demoService = (DemoService) referenceBean.getObject(factory);

		// test
        UserDTO userDTO = demoService.sayHi("[SYNC]jack");
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
	public static void testFUTURE(XxlRpcFactory factory) throws Exception {
		// client test
		XxlRpcReferenceBean referenceBean = new XxlRpcReferenceBean();
		referenceBean.setClient(NettyClient.class);
		referenceBean.setSerializer(HessianSerializer.class);
		referenceBean.setCallType(CallType.FUTURE);
		referenceBean.setLoadBalance(LoadBalance.ROUND);
		referenceBean.setIface(DemoService.class);
		referenceBean.setVersion(null);
		referenceBean.setTimeout(500);
		referenceBean.setAppname("server01");
		//referenceBean.setAddress("127.0.0.1:7080");
		//referenceBean.setAccessToken(null);

		DemoService demoService = (DemoService) referenceBean.getObject(factory);

		// test
		demoService.sayHi("[FUTURE]jack" );
        Future<UserDTO> userDTOFuture = XxlRpcInvokeFuture.getFuture(UserDTO.class);
		UserDTO userDTO = userDTOFuture.get();

		System.out.println(userDTO.toString());
	}


	/**
	 * CallType.CALLBACK
	 */
	public static void testCALLBACK(XxlRpcFactory factory) throws Exception {
		// client test
		XxlRpcReferenceBean referenceBean = new XxlRpcReferenceBean();
		referenceBean.setClient(NettyClient.class);
		referenceBean.setSerializer(HessianSerializer.class);
		referenceBean.setCallType(CallType.CALLBACK);
		referenceBean.setLoadBalance(LoadBalance.ROUND);
		referenceBean.setIface(DemoService.class);
		referenceBean.setVersion(null);
		referenceBean.setTimeout(500);
		referenceBean.setAppname("server01");
		//referenceBean.setAddress("127.0.0.1:7080");
		//referenceBean.setAccessToken(null);

		DemoService demoService = (DemoService) referenceBean.getObject(factory);


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

        demoService.sayHi("[CALLBACK]jack");
	}


	/**
	 * CallType.ONEWAY
	 */
	public static void testONEWAY(XxlRpcFactory factory) throws Exception {
		// client test
		XxlRpcReferenceBean referenceBean = new XxlRpcReferenceBean();
		referenceBean.setClient(NettyClient.class);
		referenceBean.setSerializer(HessianSerializer.class);
		referenceBean.setCallType(CallType.ONEWAY);
		referenceBean.setLoadBalance(LoadBalance.ROUND);
		referenceBean.setIface(DemoService.class);
		referenceBean.setVersion(null);
		referenceBean.setTimeout(500);
		referenceBean.setAppname("server01");
		//referenceBean.setAddress("127.0.0.1:7080");
		//referenceBean.setAccessToken(null);

		DemoService demoService = (DemoService) referenceBean.getObject(factory);

		// test
        demoService.sayHi("[ONEWAY]jack");
	}

}
