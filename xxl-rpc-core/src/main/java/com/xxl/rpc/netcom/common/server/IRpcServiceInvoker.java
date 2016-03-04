package com.xxl.rpc.netcom.common.server;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import com.xxl.rpc.netcom.common.codec.RpcRequest;
import com.xxl.rpc.netcom.common.codec.RpcResponse;

/**
 * service invoke 
 * @author xuxueli
 */
public abstract class IRpcServiceInvoker {

	public static RpcResponse invokeService(RpcRequest request, Object serviceBean) {
    	RpcResponse response = new RpcResponse();
    	response.setRequestId(request.getRequestId());
		
        try {
        	Class<?> serviceClass = serviceBean.getClass();
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();

            /*Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(serviceBean, parameters);*/

            FastClass serviceFastClass = FastClass.create(serviceClass);
            FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
            
            Object result = serviceFastMethod.invoke(serviceBean, parameters);
            
        	response.setResult(result);
        } catch (Throwable t) {
        	t.printStackTrace();
        	response.setError(t);
        }
        
        return response;
    }
	
}
