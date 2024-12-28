package com.xxl.rpc.core.invoker.reference;

import com.xxl.rpc.core.boot.XxlRpcBootstrap;
import com.xxl.rpc.core.invoker.call.CallType;
import com.xxl.rpc.core.invoker.call.XxlRpcInvokeCallback;
import com.xxl.rpc.core.invoker.call.XxlRpcInvokeFuture;
import com.xxl.rpc.core.invoker.generic.XxlRpcGenericService;
import com.xxl.rpc.core.invoker.route.LoadBalance;
import com.xxl.rpc.core.register.entity.RegisterInstance;
import com.xxl.rpc.core.remoting.Client;
import com.xxl.rpc.core.invoker.call.XxlRpcResponseFuture;
import com.xxl.rpc.core.remoting.entity.XxlRpcRequest;
import com.xxl.rpc.core.remoting.entity.XxlRpcResponse;
import com.xxl.rpc.core.provider.ProviderFactory;
import com.xxl.rpc.core.remoting.impl.netty.client.NettyClient;
import com.xxl.rpc.core.serializer.Serializer;
import com.xxl.rpc.core.serializer.impl.JsonbSerializer;
import com.xxl.rpc.core.util.ClassUtil;
import com.xxl.rpc.core.util.XxlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * rpc reference bean, use by api
 *
 * @author xuxueli 2015-10-29 20:18:32
 */
public class XxlRpcReferenceBean {
	private static final Logger logger = LoggerFactory.getLogger(XxlRpcReferenceBean.class);
	// [tips01: save 30ms/100invoke. why why why??? with this logger, it can save lots of time.]

	/**
	 * appname of service-provider
	 */
	private String appname = null;

	/**
	 * service metadata
	 */
	private Class<?> iface = null;

	/**
	 * service version
	 */
	private String version = null;

	/**
	 * client, for network
	 */
	private Class<? extends Client> client = NettyClient.class;

	/**
	 * serializer, process request and response
	 */
	private Class<? extends Serializer> serializer = JsonbSerializer.class;

	/**
	 * call type
	 */
	private CallType callType = CallType.SYNC;

	/**
	 * flow load-balance
	 */
	private LoadBalance loadBalance = LoadBalance.RANDOM;

	/**
	 * reqeust timeout
	 */
	private long timeout = 1000;

	// set
	public void setAppname(String appname) {
		this.appname = appname;
	}
	public void setIface(Class<?> iface) {
		this.iface = iface;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public void setClient(Class<? extends Client> client) {
		this.client = client;
	}
	public void setSerializer(Class<? extends Serializer> serializer) {
		this.serializer = serializer;
	}
	public void setCallType(CallType callType) {
		this.callType = callType;
	}
	public void setLoadBalance(LoadBalance loadBalance) {
		this.loadBalance = loadBalance;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	// get
	public Serializer getSerializerInstance() {
		return serializerInstance;
	}
	public long getTimeout() {
		return timeout;
	}


	// ---------------------- instance info ----------------------

	private XxlRpcBootstrap rpcBootstrap;
	private Serializer serializerInstance;

	public void setRpcBootstrap(XxlRpcBootstrap rpcBootstrap) {
		this.rpcBootstrap = rpcBootstrap;
	}
	public XxlRpcBootstrap getRpcBootstrap() {
		return rpcBootstrap;
	}

	// ---------------------- util ----------------------

	private void valid() throws Exception {

		// valid
		if (this.appname == null) {
			throw new XxlRpcException("xxl-rpc reference appname missing.");
		}
		if (this.iface == null) {
			throw new XxlRpcException("xxl-rpc reference iface missing.");
		}
		if (this.client == null) {
			throw new XxlRpcException("xxl-rpc reference client missing.");
		}
		if (this.serializer == null) {
			throw new XxlRpcException("xxl-rpc reference serializer missing.");
		}
		if (this.callType == null) {
			throw new XxlRpcException("xxl-rpc reference callType missing.");
		}
		if (this.loadBalance == null) {
			throw new XxlRpcException("xxl-rpc reference loadBalance missing.");
		}
		if (!(this.timeout > 0 && this.timeout< 60 * 1000 )) {
			throw new XxlRpcException("xxl-rpc reference timeout invlid.");
		}

		// build instance
		if (rpcBootstrap == null) {
			throw new XxlRpcException("xxl-rpc reference rpcBootstrap missing.");
		}
		this.serializerInstance = serializer.newInstance();
	}


	public Object getObject() throws Exception {
		// do valid
		valid();

		// build proxy object
		return Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), new Class[] { iface },
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

						// filter method like "Object.toString()"
						if (Object.class.equals(method.getDeclaringClass())) {
							logger.debug(">>>>>>>>>>> xxl-rpc proxy class-method not support [{}#{}], invoking original method",
									method.getDeclaringClass().getName(), method.getName());
							return method.invoke(proxy, args);
						}

						// method param
						String className = method.getDeclaringClass().getName();	// iface.getName()
						String varsion_ = version;
						String methodName = method.getName();
						Class<?>[] parameterTypes = method.getParameterTypes();
						Object[] parameters = args;

						// method return
						//Class<?> returnType = method.getReturnType();

						// filter for generic
						if (className.equals(XxlRpcGenericService.class.getName()) && methodName.equals("invoke")) {

							Class<?>[] paramTypes = null;
							if (args[3]!=null) {
								String[] paramTypes_str = (String[]) args[3];
								if (paramTypes_str.length > 0) {
									paramTypes = new Class[paramTypes_str.length];
									for (int i = 0; i < paramTypes_str.length; i++) {
										paramTypes[i] = ClassUtil.resolveClass(paramTypes_str[i]);
									}
								}
							}

							className = (String) args[0];
							varsion_ = (String) args[1];
							methodName = (String) args[2];
							parameterTypes = paramTypes;
							parameters = (Object[]) args[4];
						}

						// discovery + load-balance
						RegisterInstance registerInstance = null;
						if (rpcBootstrap!=null && rpcBootstrap.getRegister()!=null) {
							// discovery
							String serviceKey = ProviderFactory.makeServiceKey(className, varsion_);
							TreeSet<RegisterInstance> registerInstanceSet = rpcBootstrap.getRegister().discovery(appname);

							// load balance
							if (registerInstanceSet==null || registerInstanceSet.isEmpty()) {
								// pass
							} else if (registerInstanceSet.size()==1) {
								registerInstance = registerInstanceSet.stream().findFirst().get();
							} else {
								/*Set<String> addressSet =registerInstanceSet.stream()
										.map(registerInstance -> IpUtil.getIpPort(registerInstance.getIp(), registerInstance.getPort()))
										.collect(Collectors.toSet());*/
								// route
								registerInstance = loadBalance.xxlRpcInvokerRouter.route(serviceKey, registerInstanceSet);
							}
						}
						if (registerInstance == null) {
							throw new XxlRpcException("xxl-rpc reference bean[appname="+ appname +", className="+className+"] RegisterInstance not found.");
						}

						// request
						XxlRpcRequest xxlRpcRequest = new XxlRpcRequest();
	                    xxlRpcRequest.setRequestId(UUID.randomUUID().toString());
	                    xxlRpcRequest.setCreateMillisTime(System.currentTimeMillis());
	                    xxlRpcRequest.setClassName(className);
	                    xxlRpcRequest.setMethodName(methodName);
	                    xxlRpcRequest.setParameterTypes(parameterTypes);
	                    xxlRpcRequest.setParameters(parameters);
	                    xxlRpcRequest.setVersion(version);
						//xxlRpcRequest.setAccessToken(accessToken);

	                    // do invoke
						XxlRpcResponseFuture rpcFuture = null;
						try {
							// get client instance
							Client clientInstance = rpcBootstrap.getInvoker().getClient(registerInstance, client, serializerInstance);

							// send request
							if (CallType.SYNC == callType) {
								// future-response set
								rpcFuture = new XxlRpcResponseFuture(rpcBootstrap.getInvoker(), xxlRpcRequest, null);

								// do invoke
								clientInstance.send(xxlRpcRequest);

								// future get
								XxlRpcResponse xxlRpcResponse = rpcFuture.get(timeout, TimeUnit.MILLISECONDS);
								if (xxlRpcResponse.getErrorMsg() != null) {
									throw new XxlRpcException(xxlRpcResponse.getErrorMsg());
								}
								return xxlRpcResponse.getResult();
							} else if (CallType.FUTURE == callType) {
								// future-response set
								rpcFuture = new XxlRpcResponseFuture(rpcBootstrap.getInvoker(), xxlRpcRequest, null);

								// invoke future set
								XxlRpcInvokeFuture.setFuture(new XxlRpcInvokeFuture(rpcFuture));

								// do invoke
								clientInstance.send(xxlRpcRequest);

								return null;
							} else if (CallType.CALLBACK == callType) {
								// get callback
								XxlRpcInvokeCallback invokeCallback = XxlRpcInvokeCallback.getCallback();
								if (invokeCallback == null) {
									throw new XxlRpcException("xxl-rpc XxlRpcInvokeCallback（CallType="+ CallType.CALLBACK.name() +"） cannot be null.");
								}

								// future-response set
								rpcFuture = new XxlRpcResponseFuture(rpcBootstrap.getInvoker(), xxlRpcRequest, invokeCallback);
								// do invoke
								clientInstance.send(xxlRpcRequest);

								return null;
							} else if (CallType.ONEWAY == callType) {
								// do invoke
								clientInstance.send(xxlRpcRequest);
								return null;
							} else {
								throw new XxlRpcException("xxl-rpc callType["+ callType +"] invalid");
							}

						} catch (Throwable e) {
							// future-response remove
							if (rpcFuture != null) {
								rpcFuture.removeInvokerFuture();
							}
							logger.info(">>>>>>>>>>> xxl-rpc, invoke error, registerInstance:{}, XxlRpcRequest{}", registerInstance.getUniqueKey(), xxlRpcRequest);
							throw e;
						} finally{
							// future-response remove
							if (Arrays.asList(CallType.SYNC, CallType.ONEWAY).contains(callType)) {
							if (rpcFuture != null) {
								rpcFuture.removeInvokerFuture();
							}
						}

					}
				}});

	}

}
