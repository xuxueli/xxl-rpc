package com.xxl.rpc.remoting.net.params;

/**
 * rpc call type
 *
 * @author xuxueli 2018-10-19
 */
public enum CallType {

    SYNC,

    ONEWAY,

    FUTURE,

    CALLBACK;


    public static CallType match(String name, CallType defaultCallType){
        for (CallType item : CallType.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultCallType;
    }

}
