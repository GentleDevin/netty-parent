package com.netty.gnss.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @Title:  GNSS客户端
 * @Description:
 * @Author: Devin
 * @CreateDate: 2021/02/02 17:25:23
 **/
public class GnssClient {
    private static int port = 80;

    public void connect(InetSocketAddress address){
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new GnssClientChannelInitializer());
            ChannelFuture f = b.connect(address).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws IOException{
        InetSocketAddress address = new InetSocketAddress("localhost", port);
        new GnssClient().connect(address);
    }
}
