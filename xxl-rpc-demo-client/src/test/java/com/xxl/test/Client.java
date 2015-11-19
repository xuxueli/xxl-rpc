package com.xxl.test;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.xxl.rpc.demo.model.User;
import com.xxl.rpc.demo.service.IDemoService;
import com.xxl.rpc.netcom.NetComClientProxy;
import com.xxl.rpc.netcom.NetComServerFactory.NetComTypeEnum;
import com.xxl.rpc.netcom.http.client.HttpProxy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationcontext-*.xml")
public class Client {

    @Autowired
    private IDemoService demoServiceHttp;
    
    @Test
    public void demoServiceHttpTest() {
    	System.out.println(demoServiceHttp.sayHi("jack"));
    }

    @SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
    	
    	IDemoService service1 = (IDemoService) new HttpProxy("http://localhost:8080/xxl-rpc-demo-server/xxl-rpc/demoService", IDemoService.class, "HESSIAN").getObject();
    	IDemoService service2 = (IDemoService) new NetComClientProxy(NetComTypeEnum.JETTY.name(), "127.0.0.1:9999", null, IDemoService.class, false, 1000 * 5).getObject();
    	
    	long start = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			User user = service2.sayHi("jack");
			System.out.println(i + "##" + BeanUtils.describe(user));
		}
		long end = System.currentTimeMillis();
    	System.out.println("cost:" + (end - start));
    	
    	// http: 100-784	500-1982	1000-3201
    	// netty: 100-475	500-691
    	
    	// pegion:100-384
    	
    	// xxl + poolMap 567/480/471/470/471 (470: 0)
    	// xxl - seriaMap  409/324/325/325/323	(325: - 145ms)	   	 【seria hashMap save 145ms】 // tips : save 145ms(470ms-->325ms)/100invoke. Caused by hash method in HashMap.get invoked in every invoke 
    	// xxl + logger 387/297/294/293/288/289/295/292/291 (295:-30ms) 【logger save 30ms】	  // tips: save 30ms/100invoke. why why why??? with this logger, it can save lots of time. 
    	
    	// xxl - poolMap  345/266/258/255/268/264/264/252(260:-35ms)    【pool Map  : may save 35ms/100invoke if move it to constructor, but it is necessary. cause by ConcurrentHashMap.get】
    	// xxl - 246/229/234/237/237 (least 240：-20ms ) 【future.get Map : may save 20ms/100invoke if remove and wait for channel instead, but it is necessary. cause by ConcurrentHashMap.get】 
	}

}
