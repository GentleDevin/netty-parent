package com.netty.file.multi.common;

import com.netty.file.multi.client.ClientHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @Title:
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/2/18 17:14
 */
public class FileUtil {
    private static Logger LOGGER= LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    public static final String CLIENT_UPLOAD_PATH = "C:\\Users\\gentl\\Desktop\\";
    public static final String CLIENT_FILE_PATH = "f1";
    public static final String SERVER_SAVE_PATH = "D:\\upload";


    public static UploadFile initUploadFile(File file) {
        UploadFile nettyUploadFile = new UploadFile();
        nettyUploadFile.setFileName(file.getName());
        nettyUploadFile.setFileLength(file.length());
        //nettyUploadFile.setFilePath(path);
        nettyUploadFile.setStarPos(0L);
        nettyUploadFile.setFile(file);
        return nettyUploadFile;
    }

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
