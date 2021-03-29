package com.netty.gnss.common;

/**
 * @Title: 判断进制数
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/2/26 14:23
 */
public class HexadecimalNumberUtil {

    //十进制
    private static boolean isOctNumberRex(String str){
        String validate = "\\d+";
        return str.matches(validate);
    }

    //十六进制
    private static boolean isHexNumberRex(String str){
        String validate = "(?i)[0-9a-f]+";
        return str.matches(validate);
    }

    //转换成字节
    private static byte convertByte(int intValue){
        byte byteValue=0;
        int temp = intValue % 256;
        if ( intValue < 0) {
           byteValue = (byte)(temp < -128 ? 256 + temp : temp);
        }else {
           byteValue =(byte)(temp > 127 ? temp - 256 : temp);
        }
        return byteValue;
    }


}
