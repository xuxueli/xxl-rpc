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
     * @param parameterTypes        parameter types, support pri base type like "int、java.lang.Integer、java.util.List、java.util.Map ..."
     * @param paramters             parameter values
     * @return result object,
     */
    public Object $invoke(String iface,
                         String version,
                         String method,
                         String[] parameterTypes,
                         Object[] paramters);

}