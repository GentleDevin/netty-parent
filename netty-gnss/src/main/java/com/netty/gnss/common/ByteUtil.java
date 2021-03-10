package com.netty.gnss.common;

/**
 * @Title:  字节操作工具类
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/3/9 15:52
 */
public class ByteUtil {

    /**
     * 取一个字节的高几位bit
     * @param b
     * @param length
     * @return
     */
    public static int getLeftNum(byte b,int length) {
        return b>>(8-length);
    }


    /**
     * 取一个字节的低几位bit
     * @param b
     * @param length
     * @return
     */
    public static int getRightNum(byte b,int length) {
        byte mv=(byte) (0xff>>(8-length));
        return b&mv;
    }


    /**
     * 取中间几位，包括startIndex位和endIndex位
     * @param b
     * @param startIndex
     * @param endIndex
     * @return
     */
    public static int getMidNum(byte b,int startIndex,int endIndex) {
        //先取高几位
        byte i=(byte) getLeftNum(b,endIndex+1);
        //再取低几位
        return getRightNum(i,endIndex-startIndex+1);
    }
}
