package com.xxl.rpc.core.invoker.generic;

/**
 * @author xuxueli 2018-12-04
 */
public interface XxlRpcGenericService {

    /**
     * generic invoke
     *
     * @param iface                 iface name
     * @param version               iface version
     * @param method                method name
     * @param parameterTypes        parameter types, Only support primitive types, javaBean needs to be converted to "Map<String, Object>"
     * @param paramters             parameter values
     * @return result, json format
     */
    public String $invoke(String iface,
                         String version,
                         String method,
                         String[] parameterTypes,
                         Object[] paramters);

}