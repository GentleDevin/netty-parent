package com.netty.gnss.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

/**
 * @Title: GNSS服务端
 * @Description:
 * @Author: Devin
 * @CreateDate: 2021/02/24 15:58:39
 **/
public class GnssServer {
    private static Logger logger = LogManager.getLogger(GnssServer.class);
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
                    .childHandler(new GnssServerChannelInitializer());
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
        InetSocketAddress address =  new InetSocketAddress("localhost", port);
        new GnssServer().bing(address);
    }
}
