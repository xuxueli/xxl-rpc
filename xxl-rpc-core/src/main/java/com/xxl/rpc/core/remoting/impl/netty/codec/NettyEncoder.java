package com.xxl.rpc.core.remoting.impl.netty.codec;

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
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)) {
            // serialize
            byte[] data = serializer.serialize(in);
            // write data-len
            out.writeInt(data.length);
            // write data-content
            out.writeBytes(data);
        }
    }

}