package com.netty.chat.client;

import com.netty.chat.client.handler.ClientHandler;
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
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private String nickName;

    public ClientChannelInitializer(String nickName) {
        this.nickName = nickName;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        /*// 基于换行符号
        channel.pipeline().addLast(new LineBasedFrameDecoder(1024));*/
        ChannelPipeline pipeline = channel.pipeline();
        //自定义解码器
        pipeline.addLast(new IMDecoder());
        //自定义编码器
        pipeline.addLast(new IMEncoder());
        //在管道中添加客户端具体实现方法
        pipeline.addLast(new ClientHandler(nickName));
    }

}
