package com.xxl.rpc.remoting.net.impl.mina.keepalive;

import com.xxl.rpc.remoting.net.params.Beat;
import com.xxl.rpc.remoting.net.params.XxlRpcRequest;
import com.xxl.rpc.remoting.net.params.XxlRpcResponse;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;


/**
 *
 */
public class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory {
    @Override
    public boolean isRequest(IoSession ioSession, Object message) {
        if (message instanceof XxlRpcRequest){
            if (Beat.BEAT_ID.equalsIgnoreCase(((XxlRpcRequest) message).getRequestId())){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isResponse(IoSession ioSession, Object message) {
        if (message instanceof XxlRpcResponse){
            if (Beat.BEAT_ID.equalsIgnoreCase(((XxlRpcResponse) message).getRequestId())){
                return true;
            }
        }
        return false;
    }

    @Override
    public Object getRequest(IoSession ioSession) {
        return Beat.BEAT_PING;
    }

    @Override
    public Object getResponse(IoSession ioSession, Object request) {
        return Beat.BEAT_PONG;
    }
}
