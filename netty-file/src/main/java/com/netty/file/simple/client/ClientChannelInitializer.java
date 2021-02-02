package com.netty.file.simple.client;

import com.netty.commons.file.NettyUploadFile;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import java.util.concurrent.TimeUnit;

/**
 * @Title: 客户端初始化channel
 * @Description:
 * @Author: Devin
 * @CreateDate: 2021/01/29 17:05:11
 **/
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private NettyUploadFile nettyUploadFile;

    public ClientChannelInitializer(NettyUploadFile nettyUploadFile) {
        this.nettyUploadFile = nettyUploadFile;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        //使用默认的编码解码传输
        channel.pipeline().addLast(new ObjectEncoder());
        channel.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
        //读写超时时间
        channel.pipeline().addLast(new ReadTimeoutHandler(20000, TimeUnit.MILLISECONDS));
        channel.pipeline().addLast(new WriteTimeoutHandler(20000, TimeUnit.MILLISECONDS));
        //增加传输数据的实现方法
        channel.pipeline().addLast(new ClientHandler(nettyUploadFile));

    }
}
