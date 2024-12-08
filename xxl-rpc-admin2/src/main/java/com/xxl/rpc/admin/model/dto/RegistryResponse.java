package com.xxl.rpc.admin.model.dto;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author xuxueli 2018-12-03
 */
public class RegistryResponse implements Serializable {
	public static final long serialVersionUID = 42L;

	// response code
	public static final int SUCCESS_CODE = 200;
	public static final int FAIL_CODE = 203;

	// field
	private int code;
	private String msg;
	private Map<String, TreeSet<String>> registryData;


	public RegistryResponse(){}
	public RegistryResponse(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	public RegistryResponse(Map<String, TreeSet<String>> registryData) {
		this.code = SUCCESS_CODE;
		this.registryData = registryData;
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

	public Map<String, TreeSet<String>> getRegistryData() {
		return registryData;
	}

	public void setRegistryData(Map<String, TreeSet<String>> registryData) {
		this.registryData = registryData;
	}

	@Override
	public String toString() {
		return "RegistryResponse{" +
				"code=" + code +
				", msg='" + msg + '\'' +
				", registryData=" + registryData +
				'}';
	}

}