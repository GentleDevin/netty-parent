package com.netty.gnss.common;

/**
 * @Title: 字符串工具类
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/3/17 11:47
 */
public class StringUtil {

    /**
     * @Description: 字符串每四位隔一个空格
     * @CreateDate: 2021/03/17 13:35:03
     * @param str:
     * @return: java.lang.String
     **/
    public static String splitStrByBlank(String str)  {
        String regex = "(.{4})";
        return str = str.replaceAll(regex,"$1 ");
    }
}
