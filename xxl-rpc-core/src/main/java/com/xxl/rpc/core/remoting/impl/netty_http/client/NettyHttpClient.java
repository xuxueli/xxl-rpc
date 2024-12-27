package com.xxl.rpc.core.remoting.impl.netty_http.client;

import com.xxl.rpc.core.factory.XxlRpcFactory;
import com.xxl.rpc.core.remoting.Client;
import com.xxl.rpc.core.remoting.entity.XxlRpcRequest;
import com.xxl.rpc.core.remoting.common.ConnectClient;

/**
 * netty_http client
 *
 * @author xuxueli 2015-11-24 22:25:15
 */
public class NettyHttpClient extends Client {

    private Class<? extends ConnectClient> connectClientImpl = NettyHttpConnectClient.class;

    @Override
    public void asyncSend(String address, XxlRpcRequest xxlRpcRequest, final XxlRpcFactory factory) throws Exception {
        ConnectClient.asyncSend(xxlRpcRequest, address, connectClientImpl, xxlRpcReferenceBean, factory);
    }

}
