package com.netty.gnss.common;

import java.io.*;
import java.util.Arrays;

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


    /**
     * @Description: 取一个字节的高几位bit
     * @CreateDate: 2021/03/29 09:18:50
     * @param b: 字节值
     * @param rightShiftLength: 右移长度
     * @return: long
     **/
    public static long getLeftBit(long b,int rightShiftLength) {
        return b >> rightShiftLength;
    }


    /**
     * @Description: 取一个字节的低几位bit
     * @CreateDate: 2021/03/29 09:20:20
     * @param b: 字节值
     * @param length: 一个字节的低几位长度
     * @return: int
     **/
    public static int getRightBit(long b,int length) {
        switch(length){
            case 1:
                b = b & 0x1;
                break;
            case 2:
                b = b & 0x3;
                break;
            case 3:
                b = b & 0x7;
                break;
            case 4:
                b = b & 0xf;
                break;
            case 5:
                b = b & 0x1f;
                break;
            case 6:
                b = b & 0x3f;
                break;
            case 7:
                b = b & 0x7f;
                break;
            case 8:
                b = b & 0xff;
                break;
            case 12:
                b = b & 0xfff;
                break;
            case 16:
                b = b & 0xffff;
                break;
            case 21:
                b = b & 0x1fffff;
                break;
            case 28:
                b = b & 0xfffffff;
                break;
            default:
                break;
        }
        return (int) b;
    }

    /***
     * @Description:  本次字段读取位 + 上个字段剩余位
     * @CreateDate: 2021/03/15 14:29:09
     * @param rangOri: 本次字段值
     * @param leftLength: 本次字段位长度
     * @param oldBit: 上次字段值
     * @param oldBitLength: 上次字段位长度
     * @return: long
     **/
    public static long getMidBit(long rangOri, int leftLength, long oldBit,int oldBitLength) {
        //本次字段值
        long leftBit = getRightBit(rangOri,leftLength);
        leftBit <<= oldBitLength;
        long midBit = leftBit | oldBit;
        return  midBit;
    }


    /**
     * short到字节数组的转换.
     */
    public static byte[] shortToByte(short number) {
        int temp = number;
        byte[] b = new byte[2];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位
            temp = temp >> 8;// 向右移8位
        }
        return b;
    }

    /**
     * 字节数组到short的转换.
     */
    public static short byteToShort(byte[] b) {
        short s = 0;
        short s0 = (short) (b[0] & 0xff);// 最低位
        short s1 = (short) (b[1] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }

    /**
     * byte转换为short类型
     *
     * @param arr
     * @return
     */
    public static short byte16ToShort(byte[] arr) {
        if (arr == null || arr.length != 16) {
            throw new IllegalArgumentException("byte数组必须不为空,并且长度为16!");
        }
        short sum = 0;
        for (int i = 0; i < 16; ++i) {
            sum |= (arr[i] << (15 - i));
        }
        return sum;
    }



    /**
     * int到字节数组的转换.
     */
    public static byte[] intToByte(int number) {
        int temp = number;
        byte[] b = new byte[4];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位
            temp = temp >> 8;// 向右移8位
        }
        return b;
    }

    /**
     *将int转换为32位byte.
     * 实际上每个8位byte只存储了一个0或1的数字
     * 比较浪费.
     * @param num
     * @return
     */
    public static byte[] intToByte32(int num) {
        byte[] arr = new byte[32];
        for (int i = 31; i >= 0; i--) {
            // &1 也可以改为num&0x01,表示取最地位数字.
            arr[i] = (byte) (num & 1);
            // 右移一位.
            num >>= 1;
        }
        return arr;
    }

    /**
     * 3个byte转换为int类型
     *
     * @param b
     * @return
     */
    public static int byte3ToInt(byte[] b) {
        if (b == null || b.length != 3) {
            throw new IllegalArgumentException("byte数组必须不为空,并且长度为3!");
        }
        int s = 0;
        int s0 = b[0] & 0xff;// 最低位
        int s1 = b[1] & 0xff;
        int s2 = b[2] & 0xff;
        s2 <<= 16;
        s1 <<= 8;
        s = s0 | s1 | s2;
        return  s;
    }

    /**
     * 字节数组到int的转换.
     */
    public static int byteToInt(byte[] b) {
        int s = 0;
        int s0 = b[0] & 0xff;// 最低位
        int s1 = b[1] & 0xff;
        int s2 = b[2] & 0xff;
        int s3 = b[3] & 0xff;
        s3 <<= 24;
        s2 <<= 16;
        s1 <<= 8;
        s = s0 | s1 | s2 | s3;
        return s;
    }


    /**
     * long类型转成byte数组
     */
    public static byte[] longToByte(long number) {
        long temp = number;
        byte[] b = new byte[8];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Long(temp & 0xff).byteValue();// 将最低位保存在最低位 temp = temp
            // >> 8;// 向右移8位
        }
        return b;
    }

    /**
     * 字节数组到long的转换.
     */
    public static long byte5ToLong(byte[] b) {
        long s = 0;
        // 最低位
        long s0 = b[0] & 0xff;
        long s1 = b[1] & 0xff;
        long s2 = b[2] & 0xff;
        long s3 = b[3] & 0xff;
        // 最低位
        long s4 = b[4] & 0xff;

        // s0不变
        s1 <<= 8;
        s2 <<= 16;
        s3 <<= 24;
        s4 <<= 8 * 4;
        s = s0 | s1 | s2 | s3 | s4 ;
        return s;
    }


    /**
     * 字节数组到long的转换.
     */
    public static long byteToLong(byte[] b) {
        long s = 0;
        long s0 = b[0] & 0xff;// 最低位
        long s1 = b[1] & 0xff;
        long s2 = b[2] & 0xff;
        long s3 = b[3] & 0xff;
        long s4 = b[4] & 0xff;// 最低位
        long s5 = b[5] & 0xff;
        long s6 = b[6] & 0xff;
        long s7 = b[7] & 0xff;

        // s0不变
        s1 <<= 8;
        s2 <<= 16;
        s3 <<= 24;
        s4 <<= 8 * 4;
        s5 <<= 8 * 5;
        s6 <<= 8 * 6;
        s7 <<= 8 * 7;
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
        return s;
    }

    /**
     * double到字节数组的转换.
     */
    public static byte[] doubleToByte(double num) {
        byte[] b = new byte[8];
        long l = Double.doubleToLongBits(num);
        for (int i = 0; i < 8; i++) {
            b[i] = new Long(l).byteValue();
            l = l >> 8;
        }
        return b;
    }

    /**
     * 字节数组到double的转换.
     */
    public static double getDouble(byte[] b) {
        long m;
        m = b[0];
        m &= 0xff;
        m |= ((long) b[1] << 8);
        m &= 0xffff;
        m |= ((long) b[2] << 16);
        m &= 0xffffff;
        m |= ((long) b[3] << 24);
        m &= 0xffffffffL;
        m |= ((long) b[4] << 32);
        m &= 0xffffffffffL;
        m |= ((long) b[5] << 40);
        m &= 0xffffffffffffL;
        m |= ((long) b[6] << 48);
        m &= 0xffffffffffffffL;
        m |= ((long) b[7] << 56);
        return Double.longBitsToDouble(m);
    }


    /**
     * float到字节数组的转换.
     */
    public static void floatToByte(float x) {
        //先用 Float.floatToIntBits(f)转换成int
    }

    /**
     * 字节数组到float的转换.
     */
    public static float getFloat(byte[] b) {
        // 4 bytes
        int accum = 0;
        for ( int shiftBy = 0; shiftBy < 4; shiftBy++ ) {
            accum |= (b[shiftBy] & 0xff) << shiftBy * 8;
        }
        return Float.intBitsToFloat(accum);
    }

    /**
     * char到字节数组的转换.
     */
    public static byte[] charToByte(char c){
        byte[] b = new byte[2];
        b[0] = (byte) ((c & 0xFF00) >> 8);
        b[1] = (byte) (c & 0xFF);
        return b;
    }

    /**
     * 字节数组到char的转换.
     */
    public static char byteToChar(byte[] b){
        char c = (char) (((b[0] & 0xFF) << 8) | (b[1] & 0xFF));
        return c;
    }

    /**
     * 传输过来的str为二进制8*8位
     * 需要将其按照每8位分割 分别计算
     * @param str 字符
     * @return 字节数组
     */
    public static byte[] getBytes(String str){
        // TODO 循环，每次处理8位
        int size = str.length()/8;
        //定义接收数组
        byte[] bytes = new byte[8];
        for (int i = 0; i < size; i++) {
            //每次截取8位计算
            String tmp = str.substring(8*i,8*(i+1));
            int tmpInt = Integer.parseInt(tmp,2);
            byte tmpByte = (byte)(tmpInt & 0xff);
            bytes[i] = tmpByte;
        }
        return bytes;
    }


    /**
     * string到字节数组的转换.
     */
    public static byte[] stringToByte(String str) throws UnsupportedEncodingException {
        return str.getBytes("GBK");
    }

    /**
     * 字节数组到String的转换.
     */
    public static String bytesToString(byte[] str) {
        String keyword = null;
        try {
            keyword = new String(str,"GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return keyword;
    }

    /**
     * byte-->bit
     * 将byte转换为一个长度为8的byte数组，数组每个值代表bit
     */
    public static byte[] getBooleanArray(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte)(b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

    /**
     * byte-->bit
     * 把byte转为字符串的bit
     */
    public static String byteToBit(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }

    /**
     * 二进制字符串转byte
     */
    public static byte decodeBinaryString(String byteStr) {
        int re, len;
        if (null == byteStr) {
            return 0;
        }
        len = byteStr.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (byteStr.charAt(0) == '0') {// 正数
                re = Integer.parseInt(byteStr, 2);
            } else {// 负数
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {// 4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    }


    /**
     * object到字节数组的转换
     */
    public void testObject2ByteArray() throws IOException,
            ClassNotFoundException {
        // Object obj = "";
        Integer[] obj = { 1, 3, 4 };

        // // object to bytearray
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(obj);
        byte[] bytes = bo.toByteArray();
        bo.close();
        oo.close();
        System.out.println(Arrays.toString(bytes));

        Integer[] intArr = (Integer[]) testByteArray2Object(bytes);
        System.out.println(Arrays.asList(intArr));


        byte[] b2 = intToByte(123);
        System.out.println(Arrays.toString(b2));

        int a = byteToInt(b2);
        System.out.println(a);

    }

    /**
     * 字节数组到object的转换.
     */
    private Object testByteArray2Object(byte[] bytes) throws IOException,
            ClassNotFoundException {
        // byte[] bytes = null;
        Object obj;
        // bytearray to object
        ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
        ObjectInputStream oi = new ObjectInputStream(bi);
        obj = oi.readObject();
        bi.close();
        oi.close();
        System.out.println(obj);
        return obj;
    }

    /**
     * @Description: 无符号按位解析数据
     * @CreateDate: 2021/03/29 09:15:56
     * @param bytes: 字节数组
     * @param offset: 取位开始偏移量
     * @param length: 取位长度
     * @return: long
     **/
    public static long getUnsignedValueByBits(byte[] bytes, int offset, int length) {
        long bits = 0;
        for (int i = offset; i < offset + length; i++) {
            long leftShift = bits << 1;
            long byteValue = bytes[i / 8];
            long rightShiftLength =  (7 - i % 8);
            long leftValue = byteValue >>> rightShiftLength;
            long bitValue = leftValue  & 1;
            bits = leftShift + bitValue ;

           /* bits = (bits << 1) + ((bytes[i / 8] >>> (7 - i % 8)) & 1);*/
        }
        return bits;
    }



    /**
     * @Description: 根据读取的字节长度转换对应的类型
     * @CreateDate: 2021/03/29 09:27:16
     * @param readBytes: 字节数组
     * @return: long
     **/
    public static long getBytesValue(byte[] readBytes) {
        long num = 0 ;
        switch(readBytes.length){
            case 1:
                num = readBytes[0];
                break;
            case 2:
                num = ByteUtil.byteToShort(readBytes);
                break;
            case 3:
                num = ByteUtil.byte3ToInt(readBytes);
                break;
            case 4:
                num = ByteUtil.byteToInt(readBytes);
                break;
            case 5:
                num = ByteUtil.byte5ToLong(readBytes);
                break;
            case 8:
                num = ByteUtil.byteToLong(readBytes);
                break;
            default:
                break;
        }
        return num ;
    }
}
