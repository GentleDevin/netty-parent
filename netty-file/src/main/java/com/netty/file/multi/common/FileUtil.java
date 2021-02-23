package com.netty.file.multi.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * @Title:  文件上传工具类
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/2/18 17:14
 */
public class FileUtil {
    private static Logger LOGGER= LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    /**
     * @Description:  初始化上传文件信息
     * @CreateDate: 2021/02/23 14:29:18
     * @param file: 
     * @return: com.netty.file.multi.common.UploadFile
     **/
    public static UploadFile initUploadFile(File file) {
        UploadFile nettyUploadFile = new UploadFile();
        nettyUploadFile.setFileName(file.getName());
        nettyUploadFile.setFileLength(file.length());
        //nettyUploadFile.setFilePath(path);
        nettyUploadFile.setStarPos(0L);
        nettyUploadFile.setFile(file);
        return nettyUploadFile;
    }

    /**
     * @Description:  初始化上传文件夹信息
     * @CreateDate: 2021/02/23 14:29:18
     * @param file:
     * @return: com.netty.file.multi.common.UploadFile
     **/
    public static UploadFile initUploadFolder(File file) {
        UploadFile uploadFile = new UploadFile();
        uploadFile.setFileName(file.getName());
        uploadFile.setFile(file);
        return uploadFile;
    }


    /**
     * @Description: 是否为文件
     * @CreateDate: 2021/02/19 14:58:37
     * @param file: 
     * @return: boolean
     **/
    public static boolean isFile(File file) {
        if (file.exists()) {
          if (!file.isFile()) {
                LOGGER.info("Not a file :" + file.getName());
                return false;
             }
        }
        return true;
    }

    public static String getFilePathByName(String sourceFilePath,String subFileName) {
        return sourceFilePath.substring(sourceFilePath.lastIndexOf(subFileName),sourceFilePath.length());
    }

    public static void main(String[] args) throws IOException {
        String path = "C:\\Users\\gentl\\Desktop\\aa\\bb\\test.txt";
        System.out.println("" +  path.lastIndexOf("aa"));
        System.out.println("" +  path.substring(path.lastIndexOf("aa"),path.length()));
        System.out.println(getFilePathByName(path,"aa"));
    }

}
