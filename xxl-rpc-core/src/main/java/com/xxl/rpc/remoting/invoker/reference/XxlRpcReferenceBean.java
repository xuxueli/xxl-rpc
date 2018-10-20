package com.xxl.rpc.remoting.invoker.reference;

import com.xxl.rpc.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.remoting.net.Client;
import com.xxl.rpc.remoting.net.NetEnum;
import com.xxl.rpc.remoting.net.params.CallType;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import com.xxl.rpc.serialize.Serializer;
import com.xxl.rpc.util.ThrowableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;
import java.util.UUID;

/**
 * rpc reference bean, use by api
 *
 * @author xuxueli 2015-10-29 20:18:32
 */
public class XxlRpcReferenceBean {
	private static final Logger logger = LoggerFactory.getLogger(XxlRpcReferenceBean.class);
	// [tips01: save 30ms/100invoke. why why why??? with this logger, it can save lots of time.]


	// ---------------------- config ----------------------

	private NetEnum netType;
	private Serializer serializer;
	private String address;
	private String accessToken;

	private Class<?> iface;
	private String version;

	private long timeout = 5000;	// million
	private CallType callType;


	public XxlRpcReferenceBean(NetEnum netType,
						   Serializer serializer,
						   String address,
						   String accessToken,
						   Class<?> iface,
						   String version,
						   long timeout,
						   CallType callType) {

		this.netType = netType;
		this.serializer = serializer;
		this.address = address;
		this.accessToken = accessToken;
		this.iface = iface;
		this.version = version;
		this.timeout = timeout;
		this.callType = callType;

		// init Client
		initClient();
	}

	// get
	public Serializer getSerializer() {
		return serializer;
	}
	public long getTimeout() {
		return timeout;
	}

	// ---------------------- initClient ----------------------

	Client client = null;

	private void initClient() {
		try {
			client = netType.clientClass.newInstance();
			client.init(this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	// ---------------------- util ----------------------

	public String routeAddress(){
		String addressItem = address;
		if (addressItem!=null && addressItem.trim().length()>0) {
			return addressItem;
		}

		if (XxlRpcInvokerFactory.getServiceRegistry() != null) {
			String serviceKey = XxlRpcProviderFactory.makeServiceKey(iface.getName(), version);
			TreeSet<String> addressSet = XxlRpcInvokerFactory.getServiceRegistry().discovery(serviceKey);
			if (addressSet.size() > 0) {
				addressItem = new ArrayList<String>(addressSet).get(new Random().nextInt(addressSet.size()));
			}
		}
		return addressItem;
	}

	public Object getObject() {
		return Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), new Class[] { iface },
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						String className = method.getDeclaringClass().getName();

						// filter method like "Object.toString()"
						if (Object.class.getName().equals(className)) {
							logger.info(">>>>>>>>>>> xxl-rpc proxy class-method not support [{}.{}]", className, method.getName());
							throw new RuntimeException("xxl-rpc proxy class-method not support");
						}

						// address
						String address = routeAddress();
						if (address==null || address.trim().length()==0) {
							throw new RuntimeException("xxl-rpc reference bean["+ className +"] address empty");
						}

						// request
						XxlRpcRequest xxlRpcRequest = new XxlRpcRequest();
	                    xxlRpcRequest.setRequestId(UUID.randomUUID().toString());
	                    xxlRpcRequest.setCreateMillisTime(System.currentTimeMillis());
	                    xxlRpcRequest.setAccessToken(accessToken);
	                    xxlRpcRequest.setClassName(className);
	                    xxlRpcRequest.setMethodName(method.getName());
	                    xxlRpcRequest.setParameterTypes(method.getParameterTypes());
	                    xxlRpcRequest.setParameters(args);
	                    
	                    // send
	                    XxlRpcResponse xxlRpcResponse = null;
	                    try {
							xxlRpcResponse = client.send(address, xxlRpcRequest);
						} catch (Throwable throwable) {
							xxlRpcResponse = new XxlRpcResponse();
							xxlRpcResponse.setErrorMsg(ThrowableUtil.toString(throwable));
						}
	                    
	                    // valid xxlRpcResponse
						if (xxlRpcResponse == null) {
							logger.error(">>>>>>>>>>> xxl-rpc netty xxlRpcResponse not found.");
							throw new Exception(">>>>>>>>>>> xxl-rpc netty xxlRpcResponse not found.");
						}
	                    if (xxlRpcResponse.getErrorMsg() != null) {
	                        throw new RuntimeException(xxlRpcResponse.getErrorMsg());
	                    } else {
	                        return xxlRpcResponse.getResult();
	                    }
	                   
					}
				});
	}


	public Class<?> getObjectType() {
		return iface;
	}

}
