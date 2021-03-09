package com.netty.gnss.protocol.content;

/**
 * @Title: 压缩格式原始观测数据信息
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/3/9 9:49
 */
public class RangeCmp {

    /**
     *  通道跟踪状态
     **/
    private int chTrStatus;

    /**
     * 瞬时多普勒，Hz
     **/
    private float dopp;

    /**
     *  伪距
     **/
    private int psr;


    /**
     *  ADR载波相位
     **/
    private int adr;


    /**
     * 码伪距标准差，m
     **/
    private float psrStd;
}
