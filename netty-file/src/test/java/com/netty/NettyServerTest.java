package com.netty;


import com.netty.file.simple.server.NettyServer;

/**
 * @Description TODO
 * @Author xiaomizhou
 * @Date 2020/3/13 15:38
 **/
public class NettyServerTest {

    public static void main(String[] args) {
        //启动服务
        new NettyServer().bind(9999);
    }
}
