package com.xxl.rpc.core.invoker.reference;

import com.xxl.rpc.core.factory.XxlRpcFactory;
import com.xxl.rpc.core.invoker.call.CallType;
import com.xxl.rpc.core.invoker.call.XxlRpcInvokeCallback;
import com.xxl.rpc.core.invoker.call.XxlRpcInvokeFuture;
import com.xxl.rpc.core.invoker.generic.XxlRpcGenericService;
import com.xxl.rpc.core.invoker.route.LoadBalance;
import com.xxl.rpc.core.register.model.RegisterInstance;
import com.xxl.rpc.core.remoting.Client;
import com.xxl.rpc.core.remoting.impl.netty.client.NettyClient;
import com.xxl.rpc.core.remoting.params.XxlRpcFuture;
import com.xxl.rpc.core.remoting.params.XxlRpcRequest;
import com.xxl.rpc.core.remoting.params.XxlRpcResponse;
import com.xxl.rpc.core.provider.ProviderFactory;
import com.xxl.rpc.core.serializer.Serializer;
import com.xxl.rpc.core.serializer.impl.HessianSerializer;
import com.xxl.rpc.core.util.ClassUtil;
import com.xxl.rpc.core.util.IpUtil;
import com.xxl.rpc.core.util.XxlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * rpc reference bean, use by api
 *
 * @author xuxueli 2015-10-29 20:18:32
 */
public class XxlRpcReferenceBean {
	private static final Logger logger = LoggerFactory.getLogger(XxlRpcReferenceBean.class);
	// [tips01: save 30ms/100invoke. why why why??? with this logger, it can save lots of time.]

	/**
	 * appname
	 */
	private String appname = null;

	/**
	 * service metadata
	 */
	private Class<?> iface = null;

	/**
	 * version
	 */
	private String version = null;

	/**
	 * client, for network
	 */
	private Class<? extends Client> client = NettyClient.class;

	/**
	 * serializer, process request and response
	 */
	private Class<? extends Serializer> serializer = HessianSerializer.class;

	/**
	 * request type
	 */
	private CallType callType = CallType.SYNC;

	/**
	 * flow load balance
	 */
	private LoadBalance loadBalance = LoadBalance.ROUND;

	/**
	 * service reqeust timeout
	 */
	private long timeout = 1000;


	// set
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
	public void setAppname(String appname) {
		this.appname = appname;
	}
	public void setIface(Class<?> iface) {
		this.iface = iface;
	}
	public void setVersion(String version) {
		this.version = version;
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
	public Class<?> getIface() {
		return iface;
	}


	// ---------------------- initClient ----------------------

	private Client clientInstance = null;
	private Serializer serializerInstance = null;

	public XxlRpcReferenceBean initClient() throws Exception {

		// valid
		if (this.client == null) {
			throw new XxlRpcException("xxl-rpc reference client missing.");
		}
		if (this.serializer == null) {
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

		// init serializerInstance
		this.serializerInstance = serializer.newInstance();

		// init Client
		clientInstance = client.newInstance();
		clientInstance.init(this);

		return this;
	}


	// ---------------------- util ----------------------

	public Object getObject(final XxlRpcFactory factory) throws Exception {

		// initClient
		initClient();

		// newProxyInstance
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
						String finalAddress = null;
						if (factory!=null && factory.getRegister()!=null) {
							// discovery
							String serviceKey = ProviderFactory.makeServiceKey(className, varsion_);
							Set<RegisterInstance> registerInstanceSet = factory.getRegister().discovery(appname);
							// load balance
							if (registerInstanceSet==null || registerInstanceSet.isEmpty()) {
								// pass
							} else if (registerInstanceSet.size()==1) {
								RegisterInstance registerInstance = registerInstanceSet.stream().findFirst().get();
								finalAddress = IpUtil.getIpPort(registerInstance.getIp(), registerInstance.getPort());
							} else {
								// TODO-2，need optimal performance
								Set<String> addressSet =registerInstanceSet.stream()
										.map(registerInstance -> IpUtil.getIpPort(registerInstance.getIp(), registerInstance.getPort()))
										.collect(Collectors.toSet());

								finalAddress = loadBalance.xxlRpcInvokerRouter.route(serviceKey, new TreeSet<>(addressSet));
							}

						}
						if (finalAddress==null || finalAddress.trim().length()==0) {
							throw new XxlRpcException("xxl-rpc reference bean["+ className +"] address empty");
						}

						// request
						XxlRpcRequest xxlRpcRequest = new XxlRpcRequest();
	                    xxlRpcRequest.setRequestId(UUID.randomUUID().toString());
	                    xxlRpcRequest.setCreateMillisTime(System.currentTimeMillis());
	                    //xxlRpcRequest.setAccessToken(accessToken);
	                    xxlRpcRequest.setClassName(className);
	                    xxlRpcRequest.setMethodName(methodName);
	                    xxlRpcRequest.setParameterTypes(parameterTypes);
	                    xxlRpcRequest.setParameters(parameters);
	                    xxlRpcRequest.setVersion(version);
	                    
	                    // send
						if (CallType.SYNC == callType) {
							// future-response set
							XxlRpcFuture futureResponse = new XxlRpcFuture(factory.getInvoker(), xxlRpcRequest, null);
							try {
								// do invoke
								clientInstance.asyncSend(finalAddress, xxlRpcRequest, factory);

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
							XxlRpcFuture futureResponse = new XxlRpcFuture(factory.getInvoker(), xxlRpcRequest, null);
                            try {
								// invoke future set
								XxlRpcInvokeFuture invokeFuture = new XxlRpcInvokeFuture(futureResponse);
								XxlRpcInvokeFuture.setFuture(invokeFuture);

                                // do invoke
								clientInstance.asyncSend(finalAddress, xxlRpcRequest, factory);

                                return null;
                            } catch (Exception e) {
								logger.info(">>>>>>>>>>> xxl-rpc, invoke error, address:{}, XxlRpcRequest{}", finalAddress, xxlRpcRequest);

								// future-response remove
								futureResponse.removeInvokerFuture();

								throw (e instanceof XxlRpcException)?e:new XxlRpcException(e);
                            }

						} else if (CallType.CALLBACK == callType) {

							// get callback
							XxlRpcInvokeCallback threadInvokeCallback = XxlRpcInvokeCallback.getCallback();
							if (threadInvokeCallback == null) {
								throw new XxlRpcException("xxl-rpc XxlRpcInvokeCallback（CallType="+ CallType.CALLBACK.name() +"） cannot be null.");
							}

							// future-response set
							XxlRpcFuture futureResponse = new XxlRpcFuture(factory.getInvoker(), xxlRpcRequest, threadInvokeCallback);
							try {
								clientInstance.asyncSend(finalAddress, xxlRpcRequest, factory);
							} catch (Exception e) {
								logger.info(">>>>>>>>>>> xxl-rpc, invoke error, address:{}, XxlRpcRequest{}", finalAddress, xxlRpcRequest);

								// future-response remove
								futureResponse.removeInvokerFuture();

								throw (e instanceof XxlRpcException)?e:new XxlRpcException(e);
							}

							return null;
						} else if (CallType.ONEWAY == callType) {
							clientInstance.asyncSend(finalAddress, xxlRpcRequest, factory);
                            return null;
                        } else {
							throw new XxlRpcException("xxl-rpc callType["+ callType +"] invalid");
						}

					}
				});
	}

}
