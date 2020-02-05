package com.xxl.rpc.remoting.invoker.reference;

import com.xxl.rpc.filter.Filter;
import com.xxl.rpc.filter.FilterChain;
import com.xxl.rpc.filter.impl.GenericFilter;
import com.xxl.rpc.filter.impl.MethodFilter;
import com.xxl.rpc.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.remoting.invoker.call.CallType;
import com.xxl.rpc.remoting.invoker.call.XxlRpcInvokeCallback;
import com.xxl.rpc.remoting.invoker.call.XxlRpcInvokeFuture;
import com.xxl.rpc.remoting.invoker.route.LoadBalance;
import com.xxl.rpc.remoting.net.Client;
import com.xxl.rpc.remoting.net.impl.netty.client.NettyClient;
import com.xxl.rpc.remoting.net.params.XxlRpcFutureResponse;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import com.xxl.rpc.serialize.Serializer;
import com.xxl.rpc.serialize.impl.HessianSerializer;
import com.xxl.rpc.util.XxlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
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

	private Class<? extends Client> client = NettyClient.class;
	private Class<? extends Serializer> serializer = HessianSerializer.class;
	private CallType callType = CallType.SYNC;
	private LoadBalance loadBalance = LoadBalance.ROUND;

	private Class<?> iface = null;
	private String version = null;

	private long timeout = 1000;

	private String address = null;
	private String accessToken = null;

	private XxlRpcInvokeCallback invokeCallback = null;

	private XxlRpcInvokerFactory invokerFactory = null;


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
	public void setIface(Class<?> iface) {
		this.iface = iface;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public void setInvokeCallback(XxlRpcInvokeCallback invokeCallback) {
		this.invokeCallback = invokeCallback;
	}
	public void setInvokerFactory(XxlRpcInvokerFactory invokerFactory) {
		this.invokerFactory = invokerFactory;
	}


	// get
	public Serializer getSerializerInstance() {
		return serializerInstance;
	}
	public long getTimeout() {
		return timeout;
	}

	public XxlRpcInvokerFactory getInvokerFactory() {
		return invokerFactory;
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
		if (this.invokerFactory == null) {
			this.invokerFactory = XxlRpcInvokerFactory.getInstance();
		}

		// init serializerInstance
		this.serializerInstance = serializer.newInstance();

		// init Client
		clientInstance = client.newInstance();
		clientInstance.init(this);

		return this;
	}


	// ---------------------- util ----------------------

	public Object getObject() throws Exception {

		// initClient
		initClient();

		// newProxyInstance
		return Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), new Class[] { iface },
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						List<Filter> filters = new ArrayList<>();

						filters.add(new GenericFilter());
						filters.add(new MethodFilter());

						FilterChain filterChain = new FilterChain(filters, new FilterChain.Delegate() {
                            @Override
                            public XxlRpcResponse doInvoke(XxlRpcRequest request) throws Exception {
                                String className = request.getClassName();	// iface.getName()
                                String varsion_ = request.getVersion();

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

                                // send
                                if (CallType.SYNC == callType) {
                                    // future-response set
                                    XxlRpcFutureResponse futureResponse = new XxlRpcFutureResponse(invokerFactory, request, null);
                                    try {
                                        // do invoke
                                        clientInstance.asyncSend(finalAddress, request);

                                        // future get
                                        XxlRpcResponse xxlRpcResponse = futureResponse.get(timeout, TimeUnit.MILLISECONDS);
                                        if (xxlRpcResponse.getErrorMsg() != null) {
                                            throw new XxlRpcException(xxlRpcResponse.getErrorMsg());
                                        }
                                        return xxlRpcResponse;
                                    } catch (Exception e) {
                                        logger.info(">>>>>>>>>>> xxl-rpc, invoke error, address:{}, XxlRpcRequest{}", finalAddress, request);

                                        throw (e instanceof XxlRpcException)?e:new XxlRpcException(e);
                                    } finally{
                                        // future-response remove
                                        futureResponse.removeInvokerFuture();
                                    }
                                } else if (CallType.FUTURE == callType) {
                                    // future-response set
                                    XxlRpcFutureResponse futureResponse = new XxlRpcFutureResponse(invokerFactory, request, null);
                                    try {
                                        // invoke future set
                                        XxlRpcInvokeFuture invokeFuture = new XxlRpcInvokeFuture(futureResponse);
                                        XxlRpcInvokeFuture.setFuture(invokeFuture);

                                        // do invoke
                                        clientInstance.asyncSend(finalAddress, request);

                                        return null;
                                    } catch (Exception e) {
                                        logger.info(">>>>>>>>>>> xxl-rpc, invoke error, address:{}, XxlRpcRequest{}", finalAddress, request);

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
                                    XxlRpcFutureResponse futureResponse = new XxlRpcFutureResponse(invokerFactory, request, finalInvokeCallback);
                                    try {
                                        clientInstance.asyncSend(finalAddress, request);
                                    } catch (Exception e) {
                                        logger.info(">>>>>>>>>>> xxl-rpc, invoke error, address:{}, XxlRpcRequest{}", finalAddress, request);

                                        // future-response remove
                                        futureResponse.removeInvokerFuture();

                                        throw (e instanceof XxlRpcException)?e:new XxlRpcException(e);
                                    }

                                    return null;
                                } else if (CallType.ONEWAY == callType) {
                                    clientInstance.asyncSend(finalAddress, request);
                                    return null;
                                } else {
                                    throw new XxlRpcException("xxl-rpc callType["+ callType +"] invalid");
                                }
                            }
						});

                        XxlRpcRequest request = new XxlRpcRequest();
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setCreateMillisTime(System.currentTimeMillis());
                        request.setAccessToken(accessToken);
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);
                        request.setVersion(version);

                        XxlRpcResponse xxlRpcResponse = filterChain.doFilter(request);
                        return xxlRpcResponse == null ? null : xxlRpcResponse.getResult();
					}
				});
	}

}
