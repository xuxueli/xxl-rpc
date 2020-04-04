package com.xxl.rpc.sample.client;

import com.xxl.rpc.core.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.core.remoting.invoker.call.CallType;
import com.xxl.rpc.core.remoting.invoker.call.XxlRpcInvokeCallback;
import com.xxl.rpc.core.remoting.invoker.call.XxlRpcInvokeFuture;
import com.xxl.rpc.core.remoting.invoker.reference.XxlRpcReferenceBean;
import com.xxl.rpc.core.remoting.invoker.route.LoadBalance;
import com.xxl.rpc.core.remoting.net.impl.netty.client.NettyClient;
import com.xxl.rpc.sample.api.DemoService;
import com.xxl.rpc.sample.api.dto.UserDTO;
import com.xxl.rpc.core.serialize.impl.HessianSerializer;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2018-10-21 20:48:40
 */
public class XxlRpcClientAplication {

	public static void main(String[] args) throws Exception {

		/*String serviceKey = XxlRpcProviderFactory.makeServiceKey(DemoService.class.getName(), null);
		XxlRpcInvokerFactory.getInstance().getServiceRegistry().registry(new HashSet<String>(Arrays.asList(serviceKey)), "127.0.0.1:7080");*/

		// test
		testSYNC();
		testFUTURE();
		testCALLBACK();
		testONEWAY();

		TimeUnit.SECONDS.sleep(2);

		// stop client invoker factory (default by getInstance, exist inner thread, need destory)
		XxlRpcInvokerFactory.getInstance().stop();

	}



	/**
	 * CallType.SYNC
	 */
	public static void testSYNC() throws Exception {
		// init client
		XxlRpcReferenceBean referenceBean = new XxlRpcReferenceBean();
		referenceBean.setClient(NettyClient.class);
		referenceBean.setSerializer(HessianSerializer.class);
		referenceBean.setCallType(CallType.SYNC);
		referenceBean.setLoadBalance(LoadBalance.ROUND);
		referenceBean.setIface(DemoService.class);
		referenceBean.setVersion(null);
		referenceBean.setTimeout(500);
		referenceBean.setAddress("127.0.0.1:7080");
		referenceBean.setAccessToken(null);
		referenceBean.setInvokeCallback(null);
		referenceBean.setInvokerFactory(null);

		DemoService demoService = (DemoService) referenceBean.getObject();

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
	public static void testFUTURE() throws Exception {
		// client test
		XxlRpcReferenceBean referenceBean = new XxlRpcReferenceBean();
		referenceBean.setClient(NettyClient.class);
		referenceBean.setSerializer(HessianSerializer.class);
		referenceBean.setCallType(CallType.FUTURE);
		referenceBean.setLoadBalance(LoadBalance.ROUND);
		referenceBean.setIface(DemoService.class);
		referenceBean.setVersion(null);
		referenceBean.setTimeout(500);
		referenceBean.setAddress("127.0.0.1:7080");
		referenceBean.setAccessToken(null);
		referenceBean.setInvokeCallback(null);
		referenceBean.setInvokerFactory(null);

		DemoService demoService = (DemoService) referenceBean.getObject();

		// test
		demoService.sayHi("[FUTURE]jack" );
        Future<UserDTO> userDTOFuture = XxlRpcInvokeFuture.getFuture(UserDTO.class);
		UserDTO userDTO = userDTOFuture.get();

		System.out.println(userDTO.toString());
	}


	/**
	 * CallType.CALLBACK
	 */
	public static void testCALLBACK() throws Exception {
		// client test
		XxlRpcReferenceBean referenceBean = new XxlRpcReferenceBean();
		referenceBean.setClient(NettyClient.class);
		referenceBean.setSerializer(HessianSerializer.class);
		referenceBean.setCallType(CallType.CALLBACK);
		referenceBean.setLoadBalance(LoadBalance.ROUND);
		referenceBean.setIface(DemoService.class);
		referenceBean.setVersion(null);
		referenceBean.setTimeout(500);
		referenceBean.setAddress("127.0.0.1:7080");
		referenceBean.setAccessToken(null);
		referenceBean.setInvokeCallback(null);
		referenceBean.setInvokerFactory(null);

		DemoService demoService = (DemoService) referenceBean.getObject();


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
	public static void testONEWAY() throws Exception {
		// client test
		XxlRpcReferenceBean referenceBean = new XxlRpcReferenceBean();
		referenceBean.setClient(NettyClient.class);
		referenceBean.setSerializer(HessianSerializer.class);
		referenceBean.setCallType(CallType.ONEWAY);
		referenceBean.setLoadBalance(LoadBalance.ROUND);
		referenceBean.setIface(DemoService.class);
		referenceBean.setVersion(null);
		referenceBean.setTimeout(500);
		referenceBean.setAddress("127.0.0.1:7080");
		referenceBean.setAccessToken(null);
		referenceBean.setInvokeCallback(null);
		referenceBean.setInvokerFactory(null);

		DemoService demoService = (DemoService) referenceBean.getObject();

		// test
        demoService.sayHi("[ONEWAY]jack");
	}

}
