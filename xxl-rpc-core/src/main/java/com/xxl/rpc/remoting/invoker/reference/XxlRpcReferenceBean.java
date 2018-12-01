package com.xxl.rpc.remoting.invoker.reference;

import com.xxl.rpc.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.remoting.invoker.call.CallType;
import com.xxl.rpc.remoting.invoker.call.XxlRpcInvokeCallback;
import com.xxl.rpc.remoting.invoker.call.XxlRpcInvokeFuture;
import com.xxl.rpc.remoting.net.Client;
import com.xxl.rpc.remoting.net.NetEnum;
import com.xxl.rpc.remoting.net.params.XxlRpcFutureResponse;
import com.xxl.rpc.remoting.net.params.XxlRpcFutureResponseFactory;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import com.xxl.rpc.serialize.Serializer;
import com.xxl.rpc.util.XxlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Random;
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
		this.iface = iface;
		this.version = version;
		this.timeout = timeout;
		this.address = address;
		this.accessToken = accessToken;
		this.invokeCallback = invokeCallback;
		this.invokerFactory = invokerFactory;

		// valid
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

	public String routeAddress(){
		String addressItem = address;
		if (addressItem!=null && addressItem.trim().length()>0) {
			return addressItem;
		}

		if (invokerFactory!=null && invokerFactory.getServiceRegistry()!=null) {
			String serviceKey = XxlRpcProviderFactory.makeServiceKey(iface.getName(), version);
			TreeSet<String> addressSet = invokerFactory.getServiceRegistry().discovery(serviceKey);
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
							throw new XxlRpcException("xxl-rpc proxy class-method not support");
						}

						// address
						String address = routeAddress();
						if (address==null || address.trim().length()==0) {
							throw new XxlRpcException("xxl-rpc reference bean["+ className +"] address empty");
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
						if (CallType.SYNC == callType) {
							try {
								// future set
								XxlRpcFutureResponse futureResponse = new XxlRpcFutureResponse(xxlRpcRequest, null);

								// do invoke
								client.asyncSend(address, xxlRpcRequest);

								// future get
								XxlRpcResponse xxlRpcResponse = futureResponse.get(timeout, TimeUnit.MILLISECONDS);
								if (xxlRpcResponse.getErrorMsg() != null) {
									throw new XxlRpcException(xxlRpcResponse.getErrorMsg());
								}
								return xxlRpcResponse.getResult();
							} catch (Exception e) {
								logger.info(">>>>>>>>>>> xxl-rpc, invoke error, address:{}, XxlRpcRequest{}", address, xxlRpcRequest);

								throw (e instanceof XxlRpcException)?e:new XxlRpcException(e);
							} finally{
								// remove-InvokerFuture
                                XxlRpcFutureResponseFactory.removeInvokerFuture(xxlRpcRequest.getRequestId());
							}
						} else if (CallType.FUTURE == callType) {

							// thread future set
							XxlRpcInvokeFuture invokeFuture = null;
                            try {
								// future set
								invokeFuture = new XxlRpcInvokeFuture(new XxlRpcFutureResponse(xxlRpcRequest, null));
								XxlRpcInvokeFuture.setFuture(invokeFuture);

                                // do invoke
                                client.asyncSend(address, xxlRpcRequest);

                                return null;
                            } catch (Exception e) {
								logger.info(">>>>>>>>>>> xxl-rpc, invoke error, address:{}, XxlRpcRequest{}", address, xxlRpcRequest);

								// remove-InvokerFuture
								invokeFuture.stop();

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

							try {
								// future set
								XxlRpcFutureResponse futureResponse = new XxlRpcFutureResponse(xxlRpcRequest, finalInvokeCallback);

								client.asyncSend(address, xxlRpcRequest);
							} catch (Exception e) {
								logger.info(">>>>>>>>>>> xxl-rpc, invoke error, address:{}, XxlRpcRequest{}", address, xxlRpcRequest);

								// future remove
								XxlRpcFutureResponseFactory.removeInvokerFuture(xxlRpcRequest.getRequestId());

								throw (e instanceof XxlRpcException)?e:new XxlRpcException(e);
							}

							return null;
						} else if (CallType.ONEWAY == callType) {
                            client.asyncSend(address, xxlRpcRequest);
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
