package com.netty.chat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * 自定义IM协议的编码器，消息的编解码基于第三方库msgpack
 */
public class IMEncoder extends MessageToByteEncoder<IMMessage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, IMMessage msg, ByteBuf out)
			throws Exception {
		out.writeBytes(new MessagePack().write(msg));
	}
	
	public String encode(IMMessage msg){
		if(null == msg){ return ""; }
		String prex = "[" + msg.getCmd() + "]" + "[" + msg.getTime() + "]";
		//[LOGIN][1612408385359][Cover][Console]
		if(IMP.LOGIN.getName().equals(msg.getCmd())){
			prex += ("[" + msg.getSender() + "][" + msg.getTerminal() + "]");
		}else if(IMP.CHAT.getName().equals(msg.getCmd())){
			prex += ("[" + msg.getSender() + "][" + msg.getReceiver()+ "]");
		}else if(IMP.SYSTEM.getName().equals(msg.getCmd())){
			prex += ("[" + msg.getOnline() + "]");
		}
		if(!(null == msg.getContent() || "".equals(msg.getContent()))){
			prex += (" - " + msg.getContent());
		}
		return prex;
	}

}
