package com.netty.file.multi.processor;

import com.netty.file.multi.common.FileConfig;
import com.netty.file.multi.common.FileUtil;
import com.netty.file.multi.common.UploadFile;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @Title: 文件上传处理类
 * @Description: 处理客户端和服务端文件上传
 * @Author: Devin
 * CreateDate: 2021/2/18 17:25
 */
public class FileProcessor {
    private static Logger LOGGER= LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

     /**
     *  每次实际读完文件字节大小
      *  byteRead <= 100KB
     **/
    private int byteRead;
    /**
     *
     *  服务端返回的读文件开始位置，也就是当前文件读取进度
     *  start = start + byteRead;
     **/
    private volatile long start = 0;
    /**
     *  文件读字节大小,默认一次读100KB，小于默认值按文件实际大小读取
     **/
    private volatile int lastLength = 0;

    private RandomAccessFile randomAccessFile;

    private UploadFile nettyUploadFile;
    /**
     *  服务端保存文件路径
     **/
    private String serverFilePath;

    public FileProcessor() {
    }

    public FileProcessor(UploadFile nettyUploadFile) {
        this.nettyUploadFile = nettyUploadFile;
    }

    /**
     * @Description: 客户端发送文件具体实现
     * @CreateDate: 2021/02/23 11:29:20
     * @param ctx:
     * @return: void
     **/
    public void clientSendFile(ChannelHandlerContext ctx) throws Exception {
        try {
            randomAccessFile = new RandomAccessFile(nettyUploadFile.getFile(), "r");
            randomAccessFile.seek(nettyUploadFile.getStarPos());
            Long fileLength = nettyUploadFile.getFileLength();
            //文件大小是否大于100KB
            if (fileLength >= 1024 * 100) {
                lastLength = 1024 * 100;
            } else {
                //小于100KB读文件大小
                lastLength = fileLength.intValue();
            }
            byte[] bytes = new byte[lastLength];
            if ((byteRead = randomAccessFile.read(bytes)) != -1) {
                nettyUploadFile.setLastLength(lastLength);
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
     * @Description:  读取服务端文件上传信息
     * @CreateDate: 2021/02/23 14:02:41
     * @param ctx: 
     * @param uploadFile: 
     * @return: void
     **/
    public void clientReadFile(ChannelHandlerContext ctx, UploadFile uploadFile) throws Exception {
        try {
            Long fileLength = uploadFile.getFileLength();
            start = uploadFile.getStarPos();
            lastLength = uploadFile.getLastLength();
            if (start != -1 && start != fileLength) {
                randomAccessFile = new RandomAccessFile(uploadFile.getFile(), "r");
                //将文件定位到start
                randomAccessFile.seek(start);
                //每次读完文件剩余大小
                Long a = randomAccessFile.length() - start;
                if (a < lastLength) {
                    lastLength = a.intValue();
                    uploadFile.setLastLength(lastLength);
                }
                byte[] bytes = new byte[lastLength];
                if ((byteRead = randomAccessFile.read(bytes)) != -1 && a > 0) {
                    uploadFile.setEndPos(byteRead);
                    uploadFile.setBytes(bytes);
                    ctx.writeAndFlush(uploadFile);
                } else {
                    randomAccessFile.close();
                    //ctx.close();
                    LOGGER.info(nettyUploadFile.getFileName() + " 文件上传成功");
                }
            } else {
                //ctx.close();
                LOGGER.info(nettyUploadFile.getFileName() + " 文件上传成功");
            }
        } finally {
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        }
    }

    /**
     * @Description: 服务端写文件
     * @CreateDate: 2021/02/23 11:41:06
     * @param ctx: 
     * @param uploadFile: 
     * @return: void
     **/
    public void serverReadFile(ChannelHandlerContext ctx, UploadFile uploadFile) throws Exception {
        try {
            //开始对上传文件进行处理
            byte[] bytes = uploadFile.getBytes();
            byteRead = uploadFile.getEndPos();
            Long fileLength = uploadFile.getFileLength();
            long starPos = uploadFile.getStarPos();
            //构建文件存储路径
            String fileName = uploadFile.getFileName();
            File file = new File(serverFilePath);
            //判断文件是否存在
            if (file.exists()) {
                //获取服务器保存文件大小
                long saveFileLength = file.length();
                //文件存在且已经传输完成
                if (saveFileLength == fileLength) {
                    uploadFile.setStarPos(saveFileLength);
                    ctx.writeAndFlush(uploadFile);
                    //文件没有传输完成
                } else if (saveFileLength < fileLength) {
                    start = saveFileLength;
                    //开始位置等于文件断点，开始断点续传
                    if (starPos == start) {
                        fileBreakpointUpload(ctx, bytes, fileName, file,uploadFile);
                    } else {
                        //不等于断点位置，回写当前文件大小给客户端
                        uploadFile.setStarPos(saveFileLength);
                        ctx.writeAndFlush(uploadFile);
                    }
                }
            } else {
                start = uploadFile.getStarPos();
                //文件不存在，从文件位置0开始上传
                fileBreakpointUpload(ctx, bytes, fileName, file,uploadFile);
            }
        } finally {
            if (randomAccessFile != null){
                randomAccessFile.close();
            }
        }
    }

    /**
     * @Description: 文件上传、断点续传
     * @CreateDate: 2021/02/01 15:17:22
     * @param ctx:
     * @param bytes:
     * @param fileName:
     * @param file:
     * @return: void
     **/
    private void fileBreakpointUpload(ChannelHandlerContext ctx, byte[] bytes, String fileName, File file,UploadFile uploadFile) throws Exception {
        //r: 只读模式 rw:读写模式
        randomAccessFile = new RandomAccessFile(file, "rw");
        //移动文件记录指针的位置, 程序将从start字节开始写数据
        randomAccessFile.seek(start);
        randomAccessFile.write(bytes);
        //下一次文件开始的位置
        start = start + byteRead;
        if (byteRead > 0) {
            //向客户端发送文件新的开始位置
            randomAccessFile.close();
            if (byteRead != 1024 * 100) {
                LOGGER.info(fileName + "文件上传完成");
                /*Thread.sleep(1000);
                new ServerHandle().channelInactive(ctx);*/
            }
            uploadFile.setStarPos(start);
            ctx.writeAndFlush(uploadFile);
        } else {
            ctx.close();
        }
    }

    /**
     * @Title: 客户端发送文件夹或文件
     * @Description:
     * @Author: Devin 
     * @CreateDate: 2021/02/23 09:40:18
     * @param ctx:
     * @param file: 
     * @return: void
     **/
    public void clientSend (ChannelHandlerContext ctx,File file) throws Exception {
        //发送文件夹
        if (file.isDirectory()) {
            UploadFile uploadFile = FileUtil.initUploadFolder(file);
            ctx.writeAndFlush(uploadFile);
            clientSendFolder(ctx, file);
        }else{
            //发送文件
            clientSendFile(ctx, file);
        }
    }

    /**
     * @Description: 客户端发送文件
     * @CreateDate: 2021/02/23 11:26:24
     * @param ctx: 
     * @param file: 
     * @return: void
     **/
    public void clientSendFile (ChannelHandlerContext ctx,File file) throws Exception {
        UploadFile uploadFile = FileUtil.initUploadFile(file);
        this.nettyUploadFile = uploadFile;
        clientSendFile(ctx);
    }


    /**
     * @Description: 客户端发送文件夹
     * @CreateDate: 2021/02/23 11:26:24
     * @param ctx:
     * @param newFile:
     * @return: void
     **/
    private void clientSendFolder(ChannelHandlerContext ctx, File newFile) throws Exception {
        File[] files =newFile.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                UploadFile uploadFile = FileUtil.initUploadFolder(file);
                ctx.writeAndFlush(uploadFile);
                clientSendFolder(ctx,uploadFile.getFile());
            }else{
                //获取文件夹里面的文件
                UploadFile uploadFile = FileUtil.initUploadFile(file);
                this.nettyUploadFile = uploadFile;
                clientSendFile(ctx);
            }
        }
    }

    /**
     * @Description: 服务端接收文件夹或文件
     * @CreateDate: 2021/02/23 11:37:46
     * @param ctx: 
     * @param uploadFile: 
     * @return: void
     **/
    public void serverReceiveFile (ChannelHandlerContext ctx,UploadFile uploadFile) throws Exception {
        File file = uploadFile.getFile();
        serverFilePath = FileConfig.SERVER_SAVE_PATH + File.separator + FileUtil.getFilePathByName(file.getPath(),FileConfig.CLIENT_FILE_PATH);
        //创建目录
        if (file.isDirectory()) {
            File newFile = new File(serverFilePath);
            if(!newFile.exists()) {
                newFile.mkdir();
                LOGGER.info(newFile.getName()+"文件夹已创建");
            }else{
                LOGGER.info(newFile.getName()+"文件夹已存在");
            }
        }else{
            //上传文件
            serverReadFile(ctx, uploadFile);
        }
    }
}
