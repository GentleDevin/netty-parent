package com.netty.file.multi.processor;

import com.netty.file.multi.common.FileUtil;
import com.netty.file.multi.common.UploadFile;
import com.netty.file.multi.server.ServerHandle;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @Title:
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/2/18 17:25
 */
public class FileProcessor {
    private static Logger LOGGER= LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

     /**
     *  每次实际读完文件字节大小
     **/
    private int byteRead;
    /**
     *
     *  服务端返回的读文件开始位置
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
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     */
    public void clientChannelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            randomAccessFile = new RandomAccessFile(nettyUploadFile.getFile(), "r");
            randomAccessFile.seek(nettyUploadFile.getStarPos());
            Long fileLength = nettyUploadFile.getFileLength();
            //文件大小是否大于100KB
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
    public void clientChannelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
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
                //每次读完文件剩余大小
                Long a = randomAccessFile.length() - start;
                if (a < lastLength) {
                    lastLength = a.intValue();
                }
                System.out.println("nettyUploadFile:" + nettyUploadFile.getFile());

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
     *  当收到对方发来的数据后，就会触发，参数msg就是发来的信息，可以是基础类型，也可以是序列化的复杂对象。
     */
    public void serverChannelRead(ChannelHandlerContext ctx, UploadFile uploadFile) throws Exception {
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
            randomAccessFile.close();
            if (byteRead != 1024 * 100) {
                start = 0;
                LOGGER.info(fileName + "文件上传临时目录完成");
                /*Thread.sleep(1000);
                new ServerHandle().channelInactive(ctx);*/
            }
            ctx.writeAndFlush(start);
        } else {
            ctx.close();
        }
    }

    public void clientSend (ChannelHandlerContext ctx,File file) throws Exception {
        if (file.isDirectory()) {
            UploadFile uploadFile = FileUtil.initUploadFolder(file);
            ctx.writeAndFlush(uploadFile);
            clientSendFolder(ctx, file);
        }else{
            clientSendFile(ctx, file);
        }
    }


    public void clientSendFile (ChannelHandlerContext ctx,File file) throws Exception {
        UploadFile uploadFile = FileUtil.initUploadFile(file);
        this.nettyUploadFile = uploadFile;
        clientChannelActive(ctx);
    }


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
                clientChannelActive(ctx);
            }
        }
    }


    public void serverReceiveFile (ChannelHandlerContext ctx,UploadFile uploadFile) throws Exception {
        File file = uploadFile.getFile();
        serverFilePath = FileUtil.SERVER_SAVE_PATH + File.separator + FileUtil.getFilePathByName(file.getPath(),FileUtil.CLIENT_FILE_PATH);
        if (file.isDirectory()) {
            File newFile = new File(serverFilePath);
            if(!newFile.exists()) {
                newFile.mkdir();
            }else{
                LOGGER.info(newFile.getName()+"文件夹已存在");
            }
        }else{
            serverChannelRead(ctx, uploadFile);
        }
    }
}
