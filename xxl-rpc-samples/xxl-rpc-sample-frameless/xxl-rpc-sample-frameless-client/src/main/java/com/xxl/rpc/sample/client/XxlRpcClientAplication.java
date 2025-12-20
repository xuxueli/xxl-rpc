package com.xxl.rpc.sample.client;

import com.xxl.rpc.core.invoker.call.CallType;
import com.xxl.rpc.core.invoker.call.XxlRpcInvokeCallback;
import com.xxl.rpc.core.invoker.call.XxlRpcInvokeFuture;
import com.xxl.rpc.core.invoker.generic.XxlRpcGenericService;
import com.xxl.rpc.sample.api.DemoService;
import com.xxl.rpc.sample.api.dto.UserDTO;
import com.xxl.rpc.sample.client.conf.FramelessXxlRpcConfig;
import com.xxl.tool.core.MapTool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2018-10-21 20:48:40
 */
public class XxlRpcClientAplication {

	public static void main(String[] args) throws Exception {

		// 1、XxlRpcBootstrap start
		FramelessXxlRpcConfig.getInstance().start();

		// 2.1、build rpc referenceBean
		DemoService demoService_SYNC = FramelessXxlRpcConfig.getInstance().buildReferenceBean(CallType.SYNC, DemoService.class);
		DemoService demoService_FUTURE = FramelessXxlRpcConfig.getInstance().buildReferenceBean(CallType.FUTURE, DemoService.class);
		DemoService demoService_CALLBACK = FramelessXxlRpcConfig.getInstance().buildReferenceBean(CallType.CALLBACK, DemoService.class);
		DemoService demoService_ONEWAY = FramelessXxlRpcConfig.getInstance().buildReferenceBean(CallType.ONEWAY, DemoService.class);
        // 2.2、build genericSerivce referenceBean
        XxlRpcGenericService genericService_SYC = FramelessXxlRpcConfig.getInstance().buildReferenceBean(CallType.SYNC, XxlRpcGenericService.class);
        XxlRpcGenericService genericService_FUTURE = FramelessXxlRpcConfig.getInstance().buildReferenceBean(CallType.FUTURE, XxlRpcGenericService.class);

		// 3.1、test rpc invoke
		testSYNC(demoService_SYNC);
		testFUTURE(demoService_FUTURE);
		testCALLBACK(demoService_CALLBACK);
		testONEWAY(demoService_ONEWAY);
        // 3.2、test generic invoke
        testGenericSYNC(genericService_SYC);
        testGenericFUTURE(genericService_FUTURE);

		// 4、XxlRpcBootstrap stop
        TimeUnit.SECONDS.sleep(5);
		FramelessXxlRpcConfig.getInstance().stop();
	}


	/**
	 * CallType.SYNC
	 */
	public static void testSYNC(DemoService demoService) throws Exception {

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
	public static void testFUTURE(DemoService demoService) throws Exception {
		demoService.sayHi("[FUTURE]jack" );
        Future<UserDTO> userDTOFuture = XxlRpcInvokeFuture.getFuture(UserDTO.class);
		UserDTO userDTO = userDTOFuture.get();

		System.out.println(userDTO.toString());
	}


	/**
	 * CallType.CALLBACK
	 */
	public static void testCALLBACK(DemoService demoService) throws Exception {
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
	public static void testONEWAY(DemoService demoService) throws Exception {
        demoService.sayHi("[ONEWAY]jack");
	}

    /**
     * test generic
     */
    private static void testGenericSYNC(XxlRpcGenericService genericService) {
        String result = genericService.$invoke(
                "com.xxl.rpc.sample.server.service.generic.Demo2Service",
                null,
                "addUser",
                new String[]{
                        "com.xxl.rpc.sample.server.service.generic.User2DTO"
                },
                new Object[]{
                        MapTool.newMap(
                                "name", "jack2",
                                "word", "[SYNC - Generic]jack"
                        )
                });
        System.out.println(result);
    }

    private static void testGenericFUTURE(XxlRpcGenericService genericServiceFuture) throws ExecutionException, InterruptedException {
        genericServiceFuture.$invoke(
                "com.xxl.rpc.sample.server.service.generic.Demo2Service",
                null,
                "addUser",
                new String[]{
                        "com.xxl.rpc.sample.server.service.generic.User2DTO"
                },
                new Object[]{
                        MapTool.newMap(
                                "name", "jack2",
                                "word", "[FUTURE - Generic]jack"
                        )
                }
        );

        Future<String> resultFuture = XxlRpcInvokeFuture.getFuture(String.class);
        String result = resultFuture.get();

        System.out.println(result);
    }

}
