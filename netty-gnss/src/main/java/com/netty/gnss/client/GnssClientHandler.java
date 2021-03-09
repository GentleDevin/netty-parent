package com.netty.gnss.client;

import com.netty.gnss.protocol.IMMessage;
import com.netty.gnss.processor.ClientMsgProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @Title: 客户端处理类
 * @Description:
 * @Author: Devin
 * @CreateDate: 2021/02/03 15:53:31
 **/
@Slf4j
public class GnssClientHandler extends SimpleChannelInboundHandler<IMMessage> {
	private ChannelHandlerContext ctx;
	private ClientMsgProcessor msgProcessor = new ClientMsgProcessor();

    /**
	 * tcp链路建立成功后调用
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.ctx = ctx;
		msgProcessor.sendMsg(ctx.channel());
	}

	/**
	 * 收到消息后调用
	 * @throws IOException
	 */
	@Override
    public void channelRead0(ChannelHandlerContext ctx, IMMessage msg) throws IOException {

    }

    /**
     * 发生异常时调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	log.info("与服务器断开连接:"+cause.getMessage());
		cause.printStackTrace();
        ctx.close();
    }

}
