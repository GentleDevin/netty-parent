package com.netty.file.simple.server;

import com.netty.file.simple.entity.UploadFile;
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
    private String rootPath = "D:\\upload";


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
     *  当收到对方发来的数据后，就会触发，参数msg就是发来的信息，可以是基础类型，也可以是序列化的复杂对象。
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            //实体类验证
            if (!(msg instanceof UploadFile)) {
                return;
            }
            //开始对上传文件进行处理
            UploadFile ef = (UploadFile) msg;
            byte[] bytes = ef.getBytes();
            byteRead = ef.getEndPos();
            Long fileLength = ef.getFileLength();
            long starPos = ef.getStarPos();
            //构建文件存储路径
            String fileName = ef.getFileName();
            String path = rootPath + File.separator + fileName;
            File file = new File(path);
            //判断文件是否存在
            if (file.exists()) {
                //获取存储目录文件大小
                long saveFileLength = file.length();
                if (saveFileLength == fileLength) {
                    //文件存在且已经传输完成
                    ctx.writeAndFlush(saveFileLength);
                    LOGGER.info(fileName + ",文件上传完成");
                } else if (saveFileLength < fileLength) {
                    start = saveFileLength;
                    //开始位置等于文件断点，开始断点续传
                    if (starPos == start) {
                        fileBreakpointUpload(ctx, bytes, fileName, file);
                    } else {
                        //不等于断点位置，回写当前文件大小给客户端
                        ctx.writeAndFlush(saveFileLength);
                    }
                }
            } else {
                //文件不存在，从0开始上传
                fileBreakpointUpload(ctx, bytes, fileName, file);
            }
        } finally {
            if (randomAccessFile != null){
                randomAccessFile.close();
            }
        }
    }

    /**
     * @Description: 文件断点续传
     * @CreateDate: 2021/02/01 15:17:22
     * @param ctx:
     * @param bytes:
     * @param fileName:
     * @param file:
     * @return: void
     **/
    private void fileBreakpointUpload(ChannelHandlerContext ctx, byte[] bytes, String fileName, File file) throws Exception {
        //r: 只读模式 rw:读写模式
        randomAccessFile = new RandomAccessFile(file, "rw");
        //移动文件记录指针的位置, 程序将从start字节开始写数据
        randomAccessFile.seek(start);
        randomAccessFile.write(bytes);
        start = start + byteRead;
        if (byteRead > 0) {
            //向客户端发送文件新的开始位置
            ctx.writeAndFlush(start);
            randomAccessFile.close();
            if (byteRead != 1024 * 100) {
                start = 0;
                LOGGER.info(fileName + "文件上传临时目录完成");
                Thread.sleep(1000);
                channelInactive(ctx);
            }
        } else {
            ctx.close();
        }
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
