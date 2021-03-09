package com.netty.gnss.protocol.content;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title: Range原始观测数据信息
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/3/2 21:48
 */
@NoArgsConstructor
@Data
public class Range {
    /**
     * 卫星PRN号
     **/
    private short prnOrSlot;
    /**
     * （GLONASS 频率+ 7），
     * GPS，BDS 和 Galileo不使用
     **/
    private short glofreq;
    /**
     * 码伪距测量值，m
     **/
    private double psr;
    /**
     * 码伪距标准差，m
     **/
    private float psrStd;
    /**
    * 载波相位（积分多普勒）， 周
    **/
    private double adr;
    /**
     * 载波相位标准差，周
     **/
    private float adrStd;
    /**
     * 瞬时多普勒，Hz
     **/
    private float dopp;
    /**
     * 载噪比
     **/
    private float cOrNo;
    /**
     *  连续跟踪时间（无周跳），s
     **/
    private float locktime;
    /**
     *  通道跟踪状态
     **/
    private int chTrStatus;
}


