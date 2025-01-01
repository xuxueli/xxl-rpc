package com.xxl.rpc.core.remoting.impl.netty.codec;

import com.xxl.rpc.core.serializer.Serializer;
import com.xxl.rpc.core.util.XxlRpcException;
import com.xxl.rpc.netty.shaded.io.netty.buffer.ByteBuf;
import com.xxl.rpc.netty.shaded.io.netty.channel.ChannelHandlerContext;
import com.xxl.rpc.netty.shaded.io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * decoder
 *
 * @author xuxueli 2015-10-29 19:02:36
 */
public class NettyDecoder extends ByteToMessageDecoder {

    private final Class<?> genericClass;
    private final Serializer serializer;

    public NettyDecoder(Class<?> genericClass, final Serializer serializer) {
        this.genericClass = genericClass;
        this.serializer = serializer;
    }

    @Override
    public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // check lenth of "data-len"
        if (in.readableBytes() < 4) {
            return;
        }
        // check length of "data-content"
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0) {
            ctx.close();
        }
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;	// fix 1024k buffer splice limix
        }

        // read data
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        // deserialize to object
        Object inObject = serializer.deserialize(data, genericClass);
        if (!genericClass.isInstance(inObject)) {
            throw new XxlRpcException("NettyDecoder decode error, genericClass not match, genericClass = " + genericClass.getName()
                    + ", inObject = " + (inObject!=null?inObject.getClass().getName():null));
        }

        out.add(inObject);
    }

}
