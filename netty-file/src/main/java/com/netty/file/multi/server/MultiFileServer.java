package com.netty.file.multi.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @Title: Netty服务端文件上传
 * @Description: 上传客户端发送的文件夹或文件
 * @Author: Devin
 * @CreateDate: 2021/01/29 17:10:52
 **/
public class MultiFileServer {
    private static Logger LOGGER= LogManager.getLogger(MultiFileServer.class.getName());
    /**
     *  配置服务端NIO线程组
     **/
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel channel;
    
    /**
     * @Description: 服务端绑定端口
     * @CreateDate: 2021/02/23 11:19:05
     * @param port: 
     * @return: io.netty.channel.ChannelFuture
     **/
    public ChannelFuture bind(int port) {
        ChannelFuture channelFuture = null;
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    //非阻塞模式
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ServerChannelInitializer());
            channelFuture = b.bind(port).syncUninterruptibly();
            this.channel = channelFuture.channel();
            LOGGER.info("服务端已经启动成功");
        } catch (Exception e) {
            LOGGER.error("文件上传服务端出现异常-> ", e);
        }finally {
            //优雅的关闭netty
            //destroy();
        }
        return channelFuture;
    }

    public void destroy() {
        if (channel == null) {
            return;
        }
        channel.close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public Channel getChannel() {
        return channel;
    }

    public static void main(String[] args) {
        //启动服务
        new MultiFileServer().bind(6666);
    }
}
