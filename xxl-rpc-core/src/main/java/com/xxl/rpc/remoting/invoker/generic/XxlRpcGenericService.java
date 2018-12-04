//package com.xxl.rpc.remoting.invoker.generic;
//
///**
// * @author xuxueli 2018-12-04
// */
//public interface XxlRpcGenericService {
//
//    /**
//     * generic invoke
//     *
//     * @param iface                 iface name
//     * @param version               iface version
//     * @param method                method name
//     * @param parameterTypes        parameter types, limit base type 、Data、Map、List
//     * @param args
//     * @return
//     */
//    public Object invoke(String iface, String version, String method, String[] parameterTypes, Object[] args);
//
//
//    {
//        // for generic
//        if (className.equals(XxlRpcGenericService.class.getName())) {	// address todo
//            Class<?>[] paramTypes = null;
//            if (args[3]!=null) {
//                String[] paramTypes_str = (String[]) args[3];
//                paramTypes = new Class[paramTypes_str.length];
//                for (int i = 0; i < paramTypes_str.length; i++) {
//                    paramTypes[i] = Class.forName(paramTypes_str[i]);
//                }
//            }
//
//            xxlRpcRequest.setClassName((String) args[0]);
//            xxlRpcRequest.setMethodName((String) args[2]);
//            xxlRpcRequest.setParameterTypes(paramTypes);
//            xxlRpcRequest.setParameters((Object[]) args[4]);
//        }
//    }
//
//}
