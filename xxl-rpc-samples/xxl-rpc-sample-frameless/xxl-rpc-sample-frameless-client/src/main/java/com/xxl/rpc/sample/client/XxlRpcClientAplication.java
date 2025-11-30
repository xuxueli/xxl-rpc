package com.xxl.rpc.sample.client;

import com.xxl.rpc.core.boot.XxlRpcBootstrap;
import com.xxl.rpc.core.boot.config.BaseConfig;
import com.xxl.rpc.core.invoker.call.CallType;
import com.xxl.rpc.core.invoker.call.XxlRpcInvokeCallback;
import com.xxl.rpc.core.invoker.call.XxlRpcInvokeFuture;
import com.xxl.rpc.core.invoker.config.InvokerConfig;
import com.xxl.rpc.core.invoker.generic.XxlRpcGenericService;
import com.xxl.rpc.core.invoker.reference.XxlRpcReferenceBean;
import com.xxl.rpc.core.invoker.route.LoadBalance;
import com.xxl.rpc.core.register.entity.RegisterInstance;
import com.xxl.rpc.core.register.impl.LocalRegister;
import com.xxl.rpc.core.remoting.impl.netty.client.NettyClient;
import com.xxl.rpc.core.serializer.impl.JsonbSerializer;
import com.xxl.rpc.sample.api.DemoService;
import com.xxl.rpc.sample.api.dto.UserDTO;
import com.xxl.tool.core.MapTool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2018-10-21 20:48:40
 */
public class XxlRpcClientAplication {

	public static void main(String[] args) throws Exception {

		// 1、LocalRegister
		LocalRegister localRegister = new LocalRegister();
		localRegister.register(new RegisterInstance("test", "xxl-rpc-sample-frameless-server", "127.0.0.1", 7080, null));

		// 2、XxlRpcBootstrap
		XxlRpcBootstrap rpcBootstrap = new XxlRpcBootstrap();
		rpcBootstrap.setBaseConfig(new BaseConfig("test", "xxl-rpc-sample-frameless-client"));
		rpcBootstrap.setRegister(localRegister);
		rpcBootstrap.setInvokerConfig(new InvokerConfig(true, NettyClient.class, JsonbSerializer.class, null));

		// 3、start
		rpcBootstrap.start();

		// 4、XxlRpcReferenceBean build
		DemoService demoService_SYNC = buildReferenceBean(rpcBootstrap, CallType.SYNC, DemoService.class);
		DemoService demoService_FUTURE = buildReferenceBean(rpcBootstrap, CallType.FUTURE, DemoService.class);
		DemoService demoService_CALLBACK = buildReferenceBean(rpcBootstrap, CallType.CALLBACK, DemoService.class);
		DemoService demoService_ONEWAY = buildReferenceBean(rpcBootstrap, CallType.ONEWAY, DemoService.class);
        // genericSerivce build
        XxlRpcGenericService genericService_SYC = buildReferenceBean(rpcBootstrap, CallType.SYNC, XxlRpcGenericService.class);
        XxlRpcGenericService genericService_FUTURE = buildReferenceBean(rpcBootstrap, CallType.FUTURE, XxlRpcGenericService.class);

		// 5、test rpc invoke
		testSYNC(demoService_SYNC);
		testFUTURE(demoService_FUTURE);
		testCALLBACK(demoService_CALLBACK);
		testONEWAY(demoService_ONEWAY);
        // test generic
        testGenericSYNC(genericService_SYC);
        testGenericFUTURE(genericService_FUTURE);

		// 6、stop
        TimeUnit.SECONDS.sleep(5);
		rpcBootstrap.stop();
	}


    /**
     * build referenceBean
     */
	private static <T> T buildReferenceBean(XxlRpcBootstrap rpcBootstrap, CallType callType, Class<T> serviceCLass) throws Exception {
		XxlRpcReferenceBean referenceBean = new XxlRpcReferenceBean();
		referenceBean.setCallType(callType);
		referenceBean.setLoadBalance(LoadBalance.ROUND);
		referenceBean.setIface(serviceCLass);
		referenceBean.setVersion(null);
		referenceBean.setTimeout(500);
		referenceBean.setAppname("xxl-rpc-sample-frameless-server");
		referenceBean.setRpcBootstrap(rpcBootstrap);

        return (T) referenceBean.getObject();
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
                                "word", "[SYNC]jack - GenericS"
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
                                "word", "[FUTURE]jack - GenericF"
                        )
                }
        );

        Future<String> resultFuture = XxlRpcInvokeFuture.getFuture(String.class);
        String result = resultFuture.get();

        System.out.println(result);
    }

}
