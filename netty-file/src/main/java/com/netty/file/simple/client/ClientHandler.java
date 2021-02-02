package com.netty.file.simple.client;

import com.netty.commons.file.NettyUploadFile;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.RandomAccessFile;

/**
 * @Title: 客户端文件上传具体实现方法
 * @Description:
 * @Author: Devin
 * @CreateDate: 2021/01/29 17:05:54
 **/
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private static Logger LOGGER= LogManager.getLogger(ClientHandler.class.getName());
    private NettyUploadFile nettyUploadFile;
    private int byteRead;
    private volatile long start = 0;
    private volatile int lastLength = 0;
    public RandomAccessFile randomAccessFile;


    public ClientHandler(NettyUploadFile nettyUploadFile) {
        if (nettyUploadFile.getFile().exists()) {
            if (!nettyUploadFile.getFile().isFile()) {
                LOGGER.info("Not a file :" + nettyUploadFile.getFileName());
                return;
            }
        }
        this.nettyUploadFile = nettyUploadFile;
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
     * channelReadComplete channel 通道 Read 读取 Complete 完成
     * 在通道读取完成后会在这个方法里通知，对应可以做刷新操作 ctx.flush()
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("客户端读取通道完成" );
        ctx.flush();
    }

    /**
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("客户端向服务端开始传输数据...");
        try {
            randomAccessFile = new RandomAccessFile(nettyUploadFile.getFile(), "r");
            randomAccessFile.seek(nettyUploadFile.getStarPos());
            Long fileLength = nettyUploadFile.getFileLength();

            if (fileLength >= 1024 * 100) {
                lastLength = 1024 * 100;
            } else {
                lastLength = fileLength.intValue();
            }

            byte[] bytes = new byte[lastLength];
            if ((byteRead = randomAccessFile.read(bytes)) != -1) {
                nettyUploadFile.setEndPos(byteRead);
                nettyUploadFile.setBytes(bytes);
                //发送消息到服务端
                ctx.writeAndFlush(nettyUploadFile);
            }
        } finally {
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        }
    }

    /**
     *  当收到对方发来的数据后，就会触发，参数msg就是发来的信息，可以是基础类型，也可以是序列化的复杂对象。
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof Long)) {
            return;
        }

        try {
            start = (Long) msg;
            Long fileLength = nettyUploadFile.getFileLength();

            if (start != -1 && start != fileLength) {
                randomAccessFile = new RandomAccessFile(nettyUploadFile.getFile(), "r");
                //将文件定位到start
                randomAccessFile.seek(start);
                Long a = randomAccessFile.length() - start;
                if (a < lastLength) {
                    lastLength = a.intValue();
                }
                byte[] bytes = new byte[lastLength];
                if ((byteRead = randomAccessFile.read(bytes)) != -1 && a > 0) {
                    nettyUploadFile.setStarPos(start);
                    nettyUploadFile.setEndPos(byteRead);
                    nettyUploadFile.setBytes(bytes);
                    ctx.writeAndFlush(nettyUploadFile);
                } else {
                    randomAccessFile.close();
                    ctx.close();
                    LOGGER.info(nettyUploadFile.getFileName() + " 文件上传成功");
                }
            } else {
                ctx.close();
                LOGGER.info(nettyUploadFile.getFileName() + " 文件上传成功");
            }
        } finally {
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        }
    }

    /**
     * 抓住异常，当发生异常的时候，可以做一些相应的处理，比如打印日志、关闭链接
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        LOGGER.error("出现异常-> " + cause.getMessage());
    }
}
