package com.xxl.rpc.remoting.net.params;

import java.io.Serializable;

/**
 * response
 *
 * @author xuxueli 2015-10-29 19:39:54
 */
public class XxlRpcResponse implements Serializable{
	private static final long serialVersionUID = 42L;
	
	private String requestId;
    private Throwable error;    // TODO，考虑改为了 String 类型
    private Object result;

    public boolean isError() {
        return error != null;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

	@Override
	public String toString() {
		return "NettyResponse [requestId=" + requestId + ", error=" + error
				+ ", result=" + result + "]";
	}
    
}
