package com.netty.gnss.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteOrder;

/**
 * @Title:	自定义IM协议的编码器
 * @Description:
 * @Author: Devin
 * @CreateDate: 2021/02/26 11:21:34
 **/
public class IMEncoder extends MessageToByteEncoder<byte[]> {


	@Override
	protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf out)
			throws Exception {
			out.writeBytes(msg);
	}
}
