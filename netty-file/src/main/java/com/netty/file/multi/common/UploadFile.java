package com.netty.file.multi.common;

import lombok.Data;

import java.io.File;
import java.io.Serializable;

/**
 * @Title: 上传实体类
 * @Description:
 * @Author: Devin
 * @CreateDate: 2021/01/29 17:03:57
 **/
@Data
public class UploadFile implements Serializable {

    //文件名
    private String fileName;
    //文件大小
    private Long fileLength;
    //文件路径
    private String filePath;
    //文件对象
    private File file;
    //开始上传位置
    private Long starPos;
    //结束上传位置
    private Integer endPos;
    //文件byte
    private byte[] bytes;
    /**
     *  文件读字节大小,默认一次读100KB，小于默认值按文件实际大小读取
     **/
    private Integer lastLength;

    /**
     *  每次实际读完文件字节大小
     *  byteRead <= 100KB
     **/
    private int byteRead;

}
