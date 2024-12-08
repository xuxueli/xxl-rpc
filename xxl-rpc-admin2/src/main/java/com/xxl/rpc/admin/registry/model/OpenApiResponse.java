package com.xxl.rpc.admin.registry.model;

import com.xxl.tool.response.ResponseCode;

import java.io.Serializable;

/**
 * @author xuxueli 2018-12-03
 */
public class OpenApiResponse<T> implements Serializable {
    public static final long serialVersionUID = 42L;

    public static final int SUCCESS_CODE = 200;
    public static final int FAIL_CODE = 203;


    private int code;

    private String msg;

    private T data;

    public OpenApiResponse() {}
    public OpenApiResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public OpenApiResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "OpenApiResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public boolean isSuccess() {
        return code == ResponseCode.CODE_200.getCode();
    }

}
