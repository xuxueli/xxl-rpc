package com.xxl.rpc.core.remoting.entity;

import java.io.Serializable;

/**
 * response
 *
 * @author xuxueli 2015-10-29 19:39:54
 */
public class XxlRpcResponse implements Serializable{
	private static final long serialVersionUID = 42L;

    /**
     * request Id
     */
	private String requestId;

    /**
     * response error message
     */
    private String errorMsg;

    /**
     * response result
     */
    private Object result;


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "XxlRpcResponse{" +
                "requestId='" + requestId + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                ", result=" + result +
                '}';
    }

}
