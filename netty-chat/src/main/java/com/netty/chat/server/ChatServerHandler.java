package com.netty.chat.server;

import com.netty.chat.processor.MsgProcessor;
import com.netty.chat.protocol.IMMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Title: 服务端处理器
 * @Description:
 * @Author: Devin 
 * @CreateDate: 2021/02/02 15:16:03
 **/
public class ChatServerHandler extends SimpleChannelInboundHandler<IMMessage> {
    private static Logger logger = LogManager.getLogger(ChatServerHandler.class);
    private MsgProcessor processor = new MsgProcessor();

    /**
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //当有客户端链接后，添加到channelGroup通信组
/*
        ChannelHandler.channelGroup.add(channel);
*/
        logger.info(channel.id() + "客户端已链接：" + channel.localAddress().toString());
    }

    /**
     * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.id() + "客户端断开链接" + ctx.channel().localAddress().toString());
        // 当有客户端退出后，从channelGroup中移除。
        /*ChannelHandler.channelGroup.remove(ctx.channel());*/
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMMessage msg) throws Exception {
        //接收msg消息
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 接收到消息：" + msg.getContent());
        //收到消息后，群发给客户端
        processor.sendMsg(ctx.channel(), msg);
/*        ChannelHandler.channelGroup.writeAndFlush(msg.getContent());*/
    }

    /**
     * 抓住异常，当发生异常的时候，可以做一些相应的处理，比如打印日志、关闭链接
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        System.out.println("异常信息：\r\n" + cause.getMessage());
    }

}
