package com.netty.chat.server;

import com.netty.chat.protocol.IMDecoder;
import com.netty.chat.protocol.IMEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @Title: 初始化Channel
 * @Description:
 * @Author: Devin
 * @CreateDate: 2021/02/02 15:09:02
 **/ 
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) {
        // 基于换行符号
        /*channel.pipeline().addLast(new LineBasedFrameDecoder(1024));*/
        ChannelPipeline pipeline = channel.pipeline();
        //自定义解码器,Inbound
        pipeline.addLast(new IMDecoder());
        //自定义编码器,Outbound
        pipeline.addLast(new IMEncoder());
        // 在管道中添加服务端具体实现方法
        pipeline.addLast(new ChatServerHandler());
    }

}
