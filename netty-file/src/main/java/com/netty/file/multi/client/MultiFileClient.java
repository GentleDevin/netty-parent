package com.netty.file.multi.client;

import com.netty.file.multi.common.FileUtil;
import com.netty.file.multi.common.UploadFile;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * @Title: 客户端文件上传
 * @Description:
 * @Author: Devin
 * @CreateDate: 2021/01/29 17:06:29
 **/
public class MultiFileClient {

    private static Logger LOGGER= LogManager.getLogger(MultiFileClient.class.getName());

    /**
     * 配置客户端NIO线程组
     **/
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel channel;

    public ChannelFuture connect(String host, int port, final UploadFile nettyUploadFile) {
        ChannelFuture channelFuture = null;
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            //连接超时时间
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
            b.handler(new ClientChannelInitializer(nettyUploadFile));
            channelFuture = b.connect(host, port).syncUninterruptibly();
            this.channel = channelFuture.channel();
        }catch (Exception e) {
            LOGGER.error("文件上传客户端出现异常-> ", e);
        }finally {
            //优雅的关闭netty
            //destroy();
        }
        return channelFuture;
    }

    public void destroy() {
        if (null == channel) {
            return;
        }
        channel.close();
        workerGroup.shutdownGracefully();
    }


    public static void main(String[] args) {
        UploadFile nettyUploadFile = FileUtil.initUploadFile(new File(FileUtil.CLIENT_UPLOAD_PATH+FileUtil.CLIENT_FILE_PATH));
        new MultiFileClient().connect("127.0.0.1", 6666, nettyUploadFile);
    }


}
