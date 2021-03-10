package com.netty.gnss.protocol.codec;

import com.netty.gnss.processor.ServerMsgProcessor;
import com.netty.gnss.protocol.IMMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * @Title: 自定义IM协议的编码器
 * @Description:
 * @Author: Devin 
 * @CreateDate: 2021/03/10 11:40:36
 **/
public class IMDecoder extends ByteToMessageDecoder {
	private static Logger logger= LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
	private final int BASE_LENGTH = 6;
	private int msgLength = 0;
	private ServerMsgProcessor msgProcessor = new ServerMsgProcessor();

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// 可读长度必须大于基本长度
		if (in.readableBytes() > BASE_LENGTH && in.readableBytes()  >= msgLength) {
			IMMessage imMessage = msgProcessor.parseGnss(in);
			if (null == imMessage) {
				return;
			}
			msgLength = imMessage.getMsgLength();
			//可读字节数据不够，等待缓冲区数据
			if(!imMessage.isReadable()) {
				return;
			}
			out.add(imMessage);
		}else{
			System.out.println("readableBytes=" +in.readableBytes());
		}
	}
}
