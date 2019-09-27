package com.xxl.rpc.remoting.net.params;

/**
 * beat for keep-alive
 *
 * @author xuxueli 2019-09-27
 */
public final class Beat {
    public static final String BEAT_ID = "BEAT_PING_PONG";

    public static XxlRpcRequest BEAT_PING;
    public static XxlRpcResponse BEAT_PONG;

    static {
        BEAT_PING = new XxlRpcRequest(){};
        BEAT_PING.setRequestId(BEAT_ID);

        BEAT_PONG = new XxlRpcResponse();
        BEAT_PONG.setRequestId(BEAT_ID);
    }

}
