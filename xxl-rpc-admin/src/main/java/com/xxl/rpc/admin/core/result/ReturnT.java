package com.xxl.rpc.admin.core.result;

/**
 * 封装返回
 * 
 * @author xuxueli 2015-3-29 18:27:32
 * @param <T>
 */
public class ReturnT<T> {
	public static final ReturnT<String> SUCCESS = new ReturnT<String>(null);
	public static final ReturnT<String> FAIL = new ReturnT<String>(500, null);
	
	private int code;
	private String msg;
	private T content;
	
	public ReturnT(T content) {
		this.code = 200;
		this.content = content;
	}
	public ReturnT(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public void setContent(T content) {
		this.content = content;
	}
	public T getContent() {
		return content;
	}
	
}
