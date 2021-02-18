package com.netty.chat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.status.StatusConfiguration;

import java.net.InetSocketAddress;

/**
 * @Title: 服务端
 * @Description: 负责客户端认证及消息转发
 * @Author: Devin 
 * @CreateDate: 2021/02/02 15:12:07
 **/
public class ChatServer {
    private static Logger logger = LogManager.getLogger(ChatServer.class);
    private static int port = 80;

    /**
     *  配置服务端NIO线程组
     **/
    public void bing(InetSocketAddress address) {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        ChannelFuture channelFuture = null;
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ServerChannelInitializer());
            channelFuture = b.bind(address).sync();
            logger.info("服务端已启动,监听端口" + address.getPort());
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        InetSocketAddress address = null;
        if(args.length > 0) {
            address = new InetSocketAddress("localhost", Integer.valueOf(args[0]));
        }else{
            address = new InetSocketAddress("localhost", port);
        }
        new ChatServer().bing(address);
    }
}
