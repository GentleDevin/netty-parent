package com.netty.io.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.charset.Charset;


public class NioServer {

    private Selector selector;
    private ServerSocketChannel socketChannel;

    public static void main(String[] args) throws IOException {
        new NioServer().bind(7397);
    }

    public void bind(int port) {
        try {
            selector = Selector.open();
            socketChannel = ServerSocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.socket().bind(new InetSocketAddress(port), 1024);
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("itstack-demo-netty nio server start done. {关注公众号：bugstack虫洞栈 | 欢迎关注&获取源码}");
            new NioServerHandler(selector, Charset.forName("UTF-8")).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
