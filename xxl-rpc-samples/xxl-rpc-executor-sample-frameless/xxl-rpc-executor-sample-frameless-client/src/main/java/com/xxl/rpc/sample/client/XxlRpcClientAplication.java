package com.xxl.rpc.sample.client;

import com.xxl.rpc.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.remoting.invoker.call.CallType;
import com.xxl.rpc.remoting.invoker.call.XxlRpcInvokeCallback;
import com.xxl.rpc.remoting.invoker.call.XxlRpcInvokeFuture;
import com.xxl.rpc.remoting.invoker.reference.XxlRpcReferenceBean;
import com.xxl.rpc.remoting.net.NetEnum;
import com.xxl.rpc.sample.api.DemoService;
import com.xxl.rpc.sample.api.dto.UserDTO;
import com.xxl.rpc.serialize.Serializer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2018-10-21 20:48:40
 */
public class XxlRpcClientAplication {

	public static void main(String[] args) throws Exception {

		// init invoker factory
		XxlRpcInvokerFactory invokerFactory = new XxlRpcInvokerFactory();
		invokerFactory.start();

		/*String serviceKey = XxlRpcProviderFactory.makeServiceKey(DemoService.class.getName(), null);		// registry local
		invokerFactory.getServiceRegistry().registry(new HashSet<String>(Arrays.asList(serviceKey)), "127.0.0.1:7080");*/

		// test
		testSYNC();
		testFUTURE();
		testCALLBACK();
		testONEWAY();

		TimeUnit.SECONDS.sleep(2);

		// stop invoker factory
		invokerFactory.stop();
	}



	/**
	 * CallType.SYNC
	 */
	public static void testSYNC(){
		// init client
		DemoService demoService = (DemoService) new XxlRpcReferenceBean(NetEnum.NETTY, Serializer.SerializeEnum.HESSIAN.getSerializer(), CallType.SYNC,
				DemoService.class, null, 500, "127.0.0.1:7080", null, null, null).getObject();

		// test
        UserDTO userDTO = demoService.sayHi("[SYNC]jack");
		System.out.println(userDTO);


		// test mult
		/*int count = 100;
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			UserDTO userDTO = demoService.sayHi("[SYNC]jack"+i );
			System.out.println(i + "##" + userDTO.toString());
		}
		long end = System.currentTimeMillis();
    	System.out.println("run count:"+ count +", cost:" + (end - start));*/

	}


	/**
	 * CallType.FUTURE
	 */
	public static void testFUTURE() throws ExecutionException, InterruptedException {
		// client test
		DemoService demoService = (DemoService) new XxlRpcReferenceBean(NetEnum.NETTY, Serializer.SerializeEnum.HESSIAN.getSerializer(), CallType.FUTURE,
				DemoService.class, null, 500, "127.0.0.1:7080", null, null, null).getObject();

		// test
		demoService.sayHi("[FUTURE]jack" );
        Future<UserDTO> userDTOFuture = XxlRpcInvokeFuture.getFuture(UserDTO.class);
		UserDTO userDTO = userDTOFuture.get();

		System.out.println(userDTO.toString());
	}


	/**
	 * CallType.CALLBACK
	 */
	public static void testCALLBACK() throws ExecutionException, InterruptedException {
		// client test
		DemoService demoService = (DemoService) new XxlRpcReferenceBean(NetEnum.NETTY, Serializer.SerializeEnum.HESSIAN.getSerializer(), CallType.CALLBACK,
				DemoService.class, null, 500, "127.0.0.1:7080", null, null, null).getObject();


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
	public static void testONEWAY() throws ExecutionException, InterruptedException {
		// client test
		DemoService demoService = (DemoService) new XxlRpcReferenceBean(NetEnum.NETTY, Serializer.SerializeEnum.HESSIAN.getSerializer(), CallType.ONEWAY,
				DemoService.class, null, 500, "127.0.0.1:7080", null, null, null).getObject();

		// test
        demoService.sayHi("[ONEWAY]jack");
	}

}
