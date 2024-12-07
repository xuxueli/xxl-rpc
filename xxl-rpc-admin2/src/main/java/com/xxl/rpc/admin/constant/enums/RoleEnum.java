package com.xxl.rpc.admin.constant.enums;

/**
 * role enum
 *
 * @author xuxueli
 */
public enum RoleEnum {

    ADMIN("ADMIN", "管理员"),
    NORMAL("NORMAL", "普通用户");

    private String value;
    private String desc;

    RoleEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    // tool
    /**
     * load by value
     *
     * @param value
     * @return
     */
    public static RoleEnum matchByValue(String value) {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (roleEnum.getValue().equals(value)) {
                return roleEnum;
            }
        }
        return null;
    }

}
