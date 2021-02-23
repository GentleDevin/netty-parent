package com.netty.file.multi.server;

import com.netty.file.multi.common.UploadFile;
import com.netty.file.multi.processor.FileProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @Title:  服务端传输文件的实现方法
 * @Description:
 * @Author: Devin
 * @CreateDate: 2021/01/29 17:10:24
 **/
public class ServerHandle extends ChannelInboundHandlerAdapter {
    private FileProcessor fileProcessor = new FileProcessor();
    private static Logger LOGGER= LogManager.getLogger(ServerHandle.class.getName());
    /**
     *  文件读字节大小,默认一次读100KB
     **/
    private int byteRead;
    /**
     *  文件已读完文件大小
     *  服务端文件读字节开始位置，每次读完一次byteRead改变
     *  start = start + byteRead;
     **/
    private volatile long start = 0;
    private RandomAccessFile randomAccessFile;

    /**
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        LOGGER.info("客户端已链接" + ctx.channel().localAddress().toString());
    }

    /**
     * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("服务端监测客户端断开链接" + ctx.channel().localAddress().toString());
        ctx.flush();
        ctx.close();
    }

    /**
     *  接收客户端数据，参数msg就是发来的信息，可以是基础类型，也可以是序列化的复杂对象。
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //实体类验证
        if (!(msg instanceof UploadFile)) {
            return;
        }
        UploadFile uploadFile = (UploadFile)msg;
        fileProcessor.serverReceiveFile(ctx,uploadFile);
    }


    /**
     * 抓住异常，当发生异常的时候，可以做一些相应的处理，比如打印日志、关闭链接
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        cause.printStackTrace();
        LOGGER.info("出现异常->" + cause.getMessage());
    }
}
