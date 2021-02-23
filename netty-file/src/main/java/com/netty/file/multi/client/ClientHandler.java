package com.netty.file.multi.client;

import com.netty.file.multi.common.UploadFile;
import com.netty.file.multi.processor.FileProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @Title: 客户端上传具体实现方法
 * @Description:  上传文件夹或文件到服务端
 * @Author: Devin
 * @CreateDate: 2021/01/29 17:05:54
 **/
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private static Logger LOGGER= LogManager.getLogger(ClientHandler.class.getName());
    private UploadFile nettyUploadFile;
    private FileProcessor fileProcessor = new FileProcessor();

    /**
     * 统计每次读文件次数
     **/
    private  int readCount = 0;
    public RandomAccessFile randomAccessFile;


    public ClientHandler(UploadFile nettyUploadFile) {
        this.nettyUploadFile = nettyUploadFile;
        fileProcessor = new FileProcessor(nettyUploadFile);
    }

    /**
     * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("客户端已断开链接" + ctx.channel().localAddress().toString());
        super.channelInactive(ctx);
    }

    /**
     * channelRead执行后触发
     * 在通道读取完成后会在这个方法里通知，对应可以做刷新操作 ctx.flush()
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("客户端读取通道完成" + ++readCount);
        ctx.flush();
    }

    /**
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("客户端向服务端开始传输数据...");
        File file = nettyUploadFile.getFile();
        fileProcessor.clientSend(ctx,file);
    }

    /**
     *  当收到对方发来的数据后，就会触发，参数msg就是发来的信息，可以是基础类型，也可以是序列化的复杂对象。
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof UploadFile)) {
            return;
        }
        UploadFile uploadFile = (UploadFile)msg;
        fileProcessor.clientReadFile(ctx,uploadFile);
    }

    /**
     * 抓住异常，当发生异常的时候，可以做一些相应的处理，比如打印日志、关闭链接
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
        LOGGER.error("出现异常-> " + cause.getMessage());
    }
}
