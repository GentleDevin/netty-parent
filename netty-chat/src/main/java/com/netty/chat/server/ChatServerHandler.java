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


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMMessage msg) throws Exception {
        if(null != msg.getContent()) {
            //接收msg消息
            System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 接收到"+msg.getSender()+"消息：" + msg.getContent());
        }
        //收到消息后，群发给客户端
        processor.sendMsg(ctx.channel(), msg);
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
