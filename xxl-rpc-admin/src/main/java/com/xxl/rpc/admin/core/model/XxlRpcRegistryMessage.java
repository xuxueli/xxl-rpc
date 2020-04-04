package com.xxl.rpc.admin.core.model;

import java.util.Date;

/**
 * @author xuxueli 2018-11-23
 */
public class XxlRpcRegistryMessage {

    private int id;
    private int type;         // 消息类型：0-注册更新
    private String data;      // 消息内容
    private Date addTime;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }
}
