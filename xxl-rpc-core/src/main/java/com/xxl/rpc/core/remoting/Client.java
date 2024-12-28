package com.xxl.rpc.core.remoting;

import com.xxl.rpc.core.boot.XxlRpcBootstrap;
import com.xxl.rpc.core.register.entity.RegisterInstance;
import com.xxl.rpc.core.remoting.entity.XxlRpcRequest;
import com.xxl.rpc.core.serializer.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xuxueli 2018-10-19
 */
public abstract class Client {
    protected static Logger logger = LoggerFactory.getLogger(Client.class);

    /**
     * init client
     *
     * @param registerInstance
     * @param serializer
     * @param rpcBootstrap
     * @throws Exception
     */
    public abstract void init(RegisterInstance registerInstance, final Serializer serializer, final XxlRpcBootstrap rpcBootstrap) throws Exception;

    /**
     * close client
     */
    public abstract void close();

    /**
     * isValidate
     *
     * @return
     */
    public abstract boolean isValidate();

    /**
     * send (async)
     *
     * @param xxlRpcRequest
     * @throws Exception
     */
    public abstract void send(XxlRpcRequest xxlRpcRequest) throws Exception ;

}
