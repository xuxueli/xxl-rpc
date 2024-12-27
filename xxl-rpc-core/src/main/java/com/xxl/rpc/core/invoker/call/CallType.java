package com.xxl.rpc.core.invoker.call;

/**
 * rpc call type
 *
 * @author xuxueli 2018-10-19
 */
public enum CallType {

    /**
     * sync call
     */
    SYNC,

    /**
     * future call
     */
    FUTURE,

    /**
     * callback call
     */
    CALLBACK,

    /**
     * oneway call
     */
    ONEWAY;

    public static CallType match(String name, CallType defaultCallType){
        for (CallType item : CallType.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultCallType;
    }

}
