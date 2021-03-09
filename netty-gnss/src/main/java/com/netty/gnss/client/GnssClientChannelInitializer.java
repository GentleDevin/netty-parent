package com.netty.gnss.client;

import com.netty.gnss.protocol.codec.IMDecoder;
import com.netty.gnss.protocol.codec.IMEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @Title: 初始化Channel
 * @Description:
 * @Author: Devin
 * @CreateDate: 2021/02/02 15:09:02
 **/ 
public class GnssClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        //自定义解码器
        pipeline.addLast(new IMDecoder());
        //自定义编码器
        pipeline.addLast(new IMEncoder());
        //在管道中添加客户端具体实现方法
        pipeline.addLast(new GnssClientHandler());
    }

}
