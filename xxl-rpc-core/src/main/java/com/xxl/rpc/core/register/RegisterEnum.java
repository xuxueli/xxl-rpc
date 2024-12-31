package com.xxl.rpc.core.register;


import com.xxl.rpc.core.register.impl.LocalRegister;
import com.xxl.rpc.core.register.impl.XxlRpcAdminRegister;

/**
 * @author xuxueli 2024-12-21
 */
public enum RegisterEnum {

    LOCAL(LocalRegister.class),
    XXL_RPC_ADMIN(XxlRpcAdminRegister.class);

    private Class<? extends Register> serializerClass;

    RegisterEnum(Class<? extends Register> serializerClass) {
        this.serializerClass = serializerClass;
    }

    public Class<? extends Register> getSerializerClass() {
        return serializerClass;
    }

    public static RegisterEnum match(String name){
        for (RegisterEnum item: RegisterEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }


}
