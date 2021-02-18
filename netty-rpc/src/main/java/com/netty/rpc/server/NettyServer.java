package com.netty.rpc.server;

import com.netty.rpc.codec.Request;
import com.netty.rpc.codec.Response;

import com.netty.rpc.codec.RpcDecoder;
import com.netty.rpc.codec.RpcEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title: Netty服务端
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/1/28 17:16
 */
public class NettyServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private String ip;
    private int port;

    public NettyServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void server() throws Exception {
        //接收请求
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //处理请求
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            final ServerBootstrap serverBootstrap = new ServerBootstrap();

            ServerBootstrap serverBootstrap1 = serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
/*
                    .option(ChannelOption.SO_SNDBUF, 32 * 1024)
*/
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024)
/*
                    .option(ChannelOption.SO_KEEPALIVE, true)
*/
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new RpcDecoder(Request.class))
                                    .addLast(new RpcEncoder(Response.class))
                                    .addLast(new NettyServerHandler());
                        }
                    });
            //开启长连接
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = serverBootstrap.bind(ip, port).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new NettyServer("127.0.0.1", 30000).server();
    }
}


