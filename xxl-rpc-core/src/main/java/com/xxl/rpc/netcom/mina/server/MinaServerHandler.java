package com.xxl.rpc.netcom.mina.server;

import java.util.Map;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.rpc.netcom.common.codec.RpcRequest;
import com.xxl.rpc.netcom.common.codec.RpcResponse;

/**
 * 消息处理器
 * @author xuxueli
 *
 */
public class MinaServerHandler extends IoHandlerAdapter {
	private static Logger logger = LoggerFactory.getLogger(MinaServerHandler.class);
	
    private final Map<String, Object> serviceMap;
    public MinaServerHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }
	
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		RpcRequest request = (RpcRequest) message;
		RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        try {
            Object result = handle(request);
            response.setResult(result);
        } catch (Throwable t) {
            response.setError(t);
        }
        session.write(response);
	}
	
	/**
     * mina server handler request
     */
    private Object handle(RpcRequest request) throws Throwable {
        String className = request.getClassName();
        Object serviceBean = serviceMap.get(className);

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        /*Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(serviceBean, parameters);*/

        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceBean, parameters);
    }
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.error(">>>>>>>>>>> xxl-rpc provider mina server caught exception", cause);
		session.close(true);
	}
}
