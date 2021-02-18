package com.netty;

import com.netty.file.simple.entity.UploadFile;
import com.netty.file.simple.client.NettyClient;

import java.io.File;

/**
 * @Description TODO
 * @Author xiaomizhou
 * @Date 2020/3/13 15:40
 **/
public class NettyClientTest {

    public static UploadFile init() {
        UploadFile nettyUploadFile = new UploadFile();
        File file = new File("C:\\Users\\gentl\\Desktop\\微信图片_20210130215351.jpg");
        nettyUploadFile.setFileName(file.getName());
        nettyUploadFile.setFileLength(file.length());
        //nettyUploadFile.setFilePath(path);
        nettyUploadFile.setStarPos(0L);
        nettyUploadFile.setFile(file);
        return nettyUploadFile;
    }


    public static void main(String[] args) {
        UploadFile nettyUploadFile = init();

        new NettyClient().connect("127.0.0.1", 9999, nettyUploadFile);
    }
}
