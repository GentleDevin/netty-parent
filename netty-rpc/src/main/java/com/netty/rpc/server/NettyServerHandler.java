package com.netty.rpc.server;

import com.google.gson.Gson;
import com.netty.rpc.codec.Request;
import com.netty.rpc.codec.Response;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


/**
 * @Title: Netty服务端处理器
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/1/28 16:50
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Request request = (Request) msg;
        System.out.println("Client Data:" + new Gson().toJson(request));

        Response response = new Response();
        response.setRequestId(request.getRequestId());
        response.setResult("Hello Client !");

       ctx.writeAndFlush(response).sync();

        // client接收到信息后主动关闭掉连接
       ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
