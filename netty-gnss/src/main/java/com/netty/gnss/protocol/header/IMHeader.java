package com.netty.gnss.protocol.header;

import com.netty.gnss.protocol.IMMessage;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title:  头部信息
 * @Description: 3 个同步字节加上 25 个头信息字节。头的长度可变，因为将来可能会追加字段。请务必检查头的长度。
 * @Author: Devin
 * CreateDate: 2021/3/5 10:54
 */
@NoArgsConstructor
@Data
public class IMHeader {
    /**
     * 3 个同步字节
     * Sync0 AA 170
     * Sync1 44 68
     * Sync2 12 18
     **/
    public static final short SYNC_0 = -86;
    public static final byte SYNC_1 = 68;
    public static final byte SYNC_2 = 18;

    /**
     * 头长度 0x1C 28
     **/
    private Byte headerLength;
    /**
     * 消息ID 如：43，140
     **/
    private Short messageID;
    /** 消息类型
     * 00 二进制
     * 01 ASCII
     * 10 简化 ASCII
     **/
    private Byte messageType;
    /**
     *  保留字段0
     **/
    private Byte reserved0;
    /**
     * 信息长度（字节），不包括 Log 头和 CRC 比特。
     **/
    private Short messageLength;
    /**
     *  保留字段1
     **/
    private Short reserved1;
    /**
     * 最近一秒中两个具有相同信息ID的log之间的处理器空闲时间。
     **/
    private Byte idleTime;
    /**
     *  GPS时间质量
     **/
    private Byte timeStatus;
    /**
     *  GPS周数
     **/
    private Short week;
     /**
      * 以ms为单位的GPS周内秒
      **/
     private Integer ms;
    /**
     * 保留字段2
     **/
    private Integer reserved2;
    /**
     * 北斗与GPS时系差
     **/
    private Short BdsToGpsTime;
    /**
     *  保留字段3
     **/
    private Short reserved3;


}
