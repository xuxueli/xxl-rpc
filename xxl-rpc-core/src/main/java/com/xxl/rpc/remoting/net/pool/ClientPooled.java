package com.xxl.rpc.remoting.net.pool;

import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.serialize.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xuxueli 2018-10-19
 */
public abstract class ClientPooled {
    protected static transient Logger logger = LoggerFactory.getLogger(ClientPooled.class);


    public abstract void init(String host, int port, Serializer serializer) throws Exception;

    public abstract void close();

    public abstract boolean isValidate();

    public abstract void send(XxlRpcRequest xxlRpcRequest) throws Exception ;


}
