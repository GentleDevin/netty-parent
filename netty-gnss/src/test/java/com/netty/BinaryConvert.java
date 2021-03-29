package com.netty;

import java.io.UnsupportedEncodingException;

/**
 * @Title:
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/3/10 15:40
 */
public class BinaryConvert {

    public static void main(String[] args) throws UnsupportedEncodingException {
        integerToAry();
    }


    public static void integerToAry() {
        String binaryStr = Integer.toBinaryString(170);
        String hexStr = Integer.toHexString(170);
        String OctalStr = Integer.toOctalString(170);

        System.out.println("binaryStr=" +binaryStr);
        System.out.println("hexStr=" +hexStr);
        System.out.println("OctalStr=" +OctalStr);

        //2进制（n进制）到10进制
        String a = "0100";
        int d = Integer.parseInt(a, 2); // 2进制
        int o = Integer.parseInt(a, 8); // 8进制
        System.out.println(d);
        System.out.println(o);

        int i = Integer.bitCount(15);
        System.out.println("i" +i);


        //10进制变为3进制（n进制）字符串
        String threeForm = Integer.toString(5, 3);
        System.out.println(threeForm);




    }


}
