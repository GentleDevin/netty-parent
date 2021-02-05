package com.netty.chat.client;

import com.netty.chat.client.handler.ClientHandler;
import com.netty.chat.server.ChatServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @Title: 客户端
 * @Description: 负责客户端消息的发送及接收
 * @Author: Devin
 * @CreateDate: 2021/02/02 17:25:23
 **/
public class ChatClient {
    private ClientHandler clientHandler;
    private static int port = 80;
    private String nickName;

    public ChatClient(String nickName){
        this.nickName = nickName;
    }
    
    public void connect(InetSocketAddress address){
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ClientChannelInitializer(nickName));
            ChannelFuture f = b.connect(address).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws IOException{
        InetSocketAddress address = null;
        if(args.length > 0) {
            address = new InetSocketAddress("localhost", Integer.valueOf(args[0]));
            new ChatClient(String.valueOf(args[1])).connect(address);
        }else{
            address = new InetSocketAddress("localhost", port);
            new ChatClient("devin").connect(address);
        }
    }
}
