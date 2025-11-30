package com.xxl.rpc.core.remoting.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

/**
 * request
 *
 * @author xuxueli 2015-10-29 19:39:12
 */
public class XxlRpcRequest implements Serializable{
	private static final long serialVersionUID = 42L;

	/**
	 * request id
	 */
	private String requestId;

	/**
	 * remote service, class name
	 */
    private String className;

    /**
     * remote service, version of className
     */
    private String version;

	/**
	 * remote service, method name
	 */
    private String methodName;

	/**
	 * remote service, method parameter types
	 */
    private Class<?>[] parameterTypes;

	/**
	 * remote service, method parameter values
	 */
    private Object[] parameters;

    private Map<String, String> extraInfo;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

    public Map<String, String> getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(Map<String, String> extraInfo) {
        this.extraInfo = extraInfo;
    }

    @Override
    public String toString() {
        return "XxlRpcRequest{" +
                "requestId='" + requestId + '\'' +
                ", className='" + className + '\'' +
                ", version='" + version + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", parameters=" + Arrays.toString(parameters) +
                ", extraInfo=" + extraInfo +
                '}';
    }

}
