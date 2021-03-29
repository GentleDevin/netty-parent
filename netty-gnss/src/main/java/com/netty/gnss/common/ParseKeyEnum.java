package com.netty.gnss.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Title: GNSS解析key名称
 * @Description:
 * @Author: Devin 
 * @CreateDate: 2021/02/26 09:37:58
 **/
@Getter
@AllArgsConstructor
public enum ParseKeyEnum {

    /**
     * 信息ID 43
     **/
    MSG_ID_43((short) 43,"RANGE"),

    /**
     * 信息ID 140
     **/
    MSG_ID_140((short) 140,"RANGECMP");


    private Short key;
    private String value;

}
