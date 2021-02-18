package com.netty.rpc.codec;

import com.netty.commons.utils.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Title: 自定义编码器
 * @Description: 使用Protostuff进行序列化和反序列化
 * @Author: Devin
 * CreateDate: 2021/1/28 16:38
 */
public class RpcEncoder extends MessageToByteEncoder {

    /**
     * 目标对象类型进行编码
     **/
    private Class<?> target;

    public RpcEncoder(Class target) {
        this.target = target;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (target.isInstance(msg)) {
            // 使用fastJson将对象转换为byte
            byte[] data = ProtostuffUtil.serialize(msg);
            // 先将消息长度写入，也就是消息头
            out.writeInt(data.length);
            // 消息体中包含我们要发送的数据
            out.writeBytes(data);
        }
    }
}
