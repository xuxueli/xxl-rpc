package com.xxl.rpc.admin.constant.enums;

/**
 * Instance Model Enum
 *
 * @author xuxueli
 */
public enum InstanceRegisterModelEnum {

    AUTO(0, "动态注册"),
    PERSISTENT(1, "持久化注册"),
    DISABLE(2, "禁用注册");

    private int value;
    private String desc;

    InstanceRegisterModelEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * match by val
     *
     * @param value
     * @return
     */
    public static InstanceRegisterModelEnum match(int value) {
        for (InstanceRegisterModelEnum instanceModelEnum : InstanceRegisterModelEnum.values()) {
            if (instanceModelEnum.getValue() == value) {
                return instanceModelEnum;
            }
        }
        return null;
    }
}
