package com.xxl.rpc.remoting.invoker.reference;

import com.xxl.rpc.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.remoting.invoker.call.CallType;
import com.xxl.rpc.remoting.invoker.call.XxlRpcInvokeCallback;
import com.xxl.rpc.remoting.invoker.call.XxlRpcInvokeFuture;
import com.xxl.rpc.remoting.invoker.generic.XxlRpcGenericService;
import com.xxl.rpc.remoting.invoker.route.LoadBalance;
import com.xxl.rpc.remoting.net.Client;
import com.xxl.rpc.remoting.net.NetEnum;
import com.xxl.rpc.remoting.net.params.XxlRpcFutureResponse;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import com.xxl.rpc.serialize.Serializer;
import com.xxl.rpc.util.ClassUtil;
import com.xxl.rpc.util.XxlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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


	// ---------------------- config ----------------------

	private NetEnum netType;
	private Serializer serializer;
	private CallType callType;
	private LoadBalance loadBalance;

	private Class<?> iface;
	private String version;

	private long timeout = 1000;

	private String address;
	private String accessToken;

	private XxlRpcInvokeCallback invokeCallback;

	private XxlRpcInvokerFactory invokerFactory;

	public XxlRpcReferenceBean(NetEnum netType,
							   Serializer serializer,
							   CallType callType,
							   LoadBalance loadBalance,
							   Class<?> iface,
							   String version,
							   long timeout,
							   String address,
							   String accessToken,
							   XxlRpcInvokeCallback invokeCallback,
							   XxlRpcInvokerFactory invokerFactory
	) {

		this.netType = netType;
		this.serializer = serializer;
		this.callType = callType;
		this.loadBalance = loadBalance;
		this.iface = iface;
		this.version = version;
		this.timeout = timeout;
		this.address = address;
		this.accessToken = accessToken;
		this.invokeCallback = invokeCallback;
		this.invokerFactory = invokerFactory;

		// valid
		if (this.netType==null) {
			throw new XxlRpcException("xxl-rpc reference netType missing.");
		}
		if (this.serializer==null) {
			throw new XxlRpcException("xxl-rpc reference serializer missing.");
		}
		if (this.callType==null) {
			throw new XxlRpcException("xxl-rpc reference callType missing.");
		}
		if (this.loadBalance==null) {
			throw new XxlRpcException("xxl-rpc reference loadBalance missing.");
		}
		if (this.iface==null) {
			throw new XxlRpcException("xxl-rpc reference iface missing.");
		}
		if (this.timeout < 0) {
			this.timeout = 0;
		}
		if (this.invokerFactory == null) {
			this.invokerFactory = XxlRpcInvokerFactory.getInstance();
		}

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

	public XxlRpcInvokerFactory getInvokerFactory() {
		return invokerFactory;
	}

	// ---------------------- initClient ----------------------

	Client client = null;

	private void initClient() {
		try {
			client = netType.clientClass.newInstance();
			client.init(this);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new XxlRpcException(e);
		}
	}


	// ---------------------- util ----------------------

	public Object getObject() {
		return Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), new Class[] { iface },
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

						// method param
						String className = method.getDeclaringClass().getName();	// iface.getName()
						String varsion_ = version;
						String methodName = method.getName();
						Class<?>[] parameterTypes = method.getParameterTypes();
						Object[] parameters = args;

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

						// filter method like "Object.toString()"
						if (className.equals(Object.class.getName())) {
							logger.info(">>>>>>>>>>> xxl-rpc proxy class-method not support [{}#{}]", className, methodName);
							throw new XxlRpcException("xxl-rpc proxy class-method not support");
						}

						// address
						String finalAddress = address;
						if (finalAddress==null || finalAddress.trim().length()==0) {
							if (invokerFactory!=null && invokerFactory.getServiceRegistry()!=null) {
								// discovery
								String serviceKey = XxlRpcProviderFactory.makeServiceKey(className, varsion_);
								TreeSet<String> addressSet = invokerFactory.getServiceRegistry().discovery(serviceKey);
								// load balance
								if (addressSet==null || addressSet.size()==0) {
									// pass
								} else if (addressSet.size()==1) {
									finalAddress = addressSet.first();
								} else {
									finalAddress = loadBalance.xxlRpcInvokerRouter.route(serviceKey, addressSet);
								}

							}
						}
						if (finalAddress==null || finalAddress.trim().length()==0) {
							throw new XxlRpcException("xxl-rpc reference bean["+ className +"] address empty");
						}

						// request
						XxlRpcRequest xxlRpcRequest = new XxlRpcRequest();
	                    xxlRpcRequest.setRequestId(UUID.randomUUID().toString());
	                    xxlRpcRequest.setCreateMillisTime(System.currentTimeMillis());
	                    xxlRpcRequest.setAccessToken(accessToken);
	                    xxlRpcRequest.setClassName(className);
	                    xxlRpcRequest.setMethodName(methodName);
	                    xxlRpcRequest.setParameterTypes(parameterTypes);
	                    xxlRpcRequest.setParameters(parameters);
	                    
	                    // send
						if (CallType.SYNC == callType) {
							// future-response set
							XxlRpcFutureResponse futureResponse = new XxlRpcFutureResponse(invokerFactory, xxlRpcRequest, null);
							try {
								// do invoke
								client.asyncSend(finalAddress, xxlRpcRequest);

								// future get
								XxlRpcResponse xxlRpcResponse = futureResponse.get(timeout, TimeUnit.MILLISECONDS);
								if (xxlRpcResponse.getErrorMsg() != null) {
									throw new XxlRpcException(xxlRpcResponse.getErrorMsg());
								}
								return xxlRpcResponse.getResult();
							} catch (Exception e) {
								logger.info(">>>>>>>>>>> xxl-rpc, invoke error, address:{}, XxlRpcRequest{}", finalAddress, xxlRpcRequest);

								throw (e instanceof XxlRpcException)?e:new XxlRpcException(e);
							} finally{
								// future-response remove
								futureResponse.removeInvokerFuture();
							}
						} else if (CallType.FUTURE == callType) {
							// future-response set
							XxlRpcFutureResponse futureResponse = new XxlRpcFutureResponse(invokerFactory, xxlRpcRequest, null);
                            try {
								// invoke future set
								XxlRpcInvokeFuture invokeFuture = new XxlRpcInvokeFuture(futureResponse);
								XxlRpcInvokeFuture.setFuture(invokeFuture);

                                // do invoke
                                client.asyncSend(finalAddress, xxlRpcRequest);

                                return null;
                            } catch (Exception e) {
								logger.info(">>>>>>>>>>> xxl-rpc, invoke error, address:{}, XxlRpcRequest{}", finalAddress, xxlRpcRequest);

								// future-response remove
								futureResponse.removeInvokerFuture();

								throw (e instanceof XxlRpcException)?e:new XxlRpcException(e);
                            }

						} else if (CallType.CALLBACK == callType) {

							// get callback
							XxlRpcInvokeCallback finalInvokeCallback = invokeCallback;
							XxlRpcInvokeCallback threadInvokeCallback = XxlRpcInvokeCallback.getCallback();
							if (threadInvokeCallback != null) {
								finalInvokeCallback = threadInvokeCallback;
							}
							if (finalInvokeCallback == null) {
								throw new XxlRpcException("xxl-rpc XxlRpcInvokeCallback（CallType="+ CallType.CALLBACK.name() +"） cannot be null.");
							}

							// future-response set
							XxlRpcFutureResponse futureResponse = new XxlRpcFutureResponse(invokerFactory, xxlRpcRequest, finalInvokeCallback);
							try {
								client.asyncSend(finalAddress, xxlRpcRequest);
							} catch (Exception e) {
								logger.info(">>>>>>>>>>> xxl-rpc, invoke error, address:{}, XxlRpcRequest{}", finalAddress, xxlRpcRequest);

								// future-response remove
								futureResponse.removeInvokerFuture();

								throw (e instanceof XxlRpcException)?e:new XxlRpcException(e);
							}

							return null;
						} else if (CallType.ONEWAY == callType) {
                            client.asyncSend(finalAddress, xxlRpcRequest);
                            return null;
                        } else {
							throw new XxlRpcException("xxl-rpc callType["+ callType +"] invalid");
						}

					}
				});
	}


	public Class<?> getObjectType() {
		return iface;
	}

}
