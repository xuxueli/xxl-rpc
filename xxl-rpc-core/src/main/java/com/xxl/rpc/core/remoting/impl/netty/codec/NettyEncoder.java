package com.xxl.rpc.core.remoting.impl.netty.codec;

import com.xxl.rpc.core.util.XxlRpcException;
import com.xxl.rpc.netty.shaded.io.netty.buffer.ByteBuf;
import com.xxl.rpc.netty.shaded.io.netty.channel.ChannelHandlerContext;
import com.xxl.rpc.netty.shaded.io.netty.handler.codec.MessageToByteEncoder;

import com.xxl.rpc.core.serializer.Serializer;

/**
 * encoder
 *
 * @author xuxueli 2015-10-29 19:43:00
 */
public class NettyEncoder extends MessageToByteEncoder<Object> {

    private Class<?> genericClass;
    private Serializer serializer;

    public NettyEncoder(Class<?> genericClass, final Serializer serializer) {
        this.genericClass = genericClass;
        this.serializer = serializer;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object inObject, ByteBuf out) throws Exception {
        // class valid
        if (!genericClass.isInstance(inObject)) {
            throw new XxlRpcException("NettyEncoder encode error, genericClass not match, genericClass = " + genericClass.getName()
                    + ", inObject = " + (inObject!=null?inObject.getClass().getName():null));
        }

        // serialize
        byte[] data = serializer.serialize(inObject);
        // write data-len
        out.writeInt(data.length);
        // write data-content
        out.writeBytes(data);
    }

}