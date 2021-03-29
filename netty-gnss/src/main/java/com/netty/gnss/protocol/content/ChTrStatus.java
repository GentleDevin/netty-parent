package com.netty.gnss.protocol.content;


import lombok.Data;

/**
 * @Title: 通道跟踪状态
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/3/23 15:29
 */
@Data
public class ChTrStatus {

    /** SV 通道号 0-n (0 = 第一个, n = 最后一个) n 视具体接收机 */
    private byte sv;
   /**
    * 载波相位有效标志
    **/
    private byte adrFlag;
    /**
     * 伪距有效标志
     **/
    private byte psrFlag;
    /**
     * 卫星系统名称
     **/
    private byte SateSys;

    /**
     * 信号类型
     **/
    private byte SignalTypes;
}
