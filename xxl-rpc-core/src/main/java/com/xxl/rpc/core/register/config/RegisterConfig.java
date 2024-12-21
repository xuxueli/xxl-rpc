package com.xxl.rpc.core.register.config;

import com.xxl.rpc.core.register.Register;

import java.util.Map;

public class RegisterConfig {

    /**
     * register class
     */
    private Register register;


    public RegisterConfig() {
    }

    public RegisterConfig(Register register) {
        this.register = register;
    }

    public Register getRegister() {
        return register;
    }

    public void setRegister(Register register) {
        this.register = register;
    }

}
