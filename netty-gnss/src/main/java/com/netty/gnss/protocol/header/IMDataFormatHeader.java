package com.netty.gnss.protocol.header;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title:  二进制数据格式头部信息
 * @Description: 3 个同步字节加上 24 个头信息字节。头的长度可变，因为将来可能会追加字段。请务必检查头的长度。
 * @Author: Devin
 * CreateDate: 2021/3/5 10:54
 */
@NoArgsConstructor
@Data
public class IMDataFormatHeader {
    /**
     * 3 个同步字节
     * Sync0 AA 170
     * Sync1 44 68
     * Sync2 B5 181
     **/
    public static final short SYNC_0 = -86;
    public static final byte SYNC_1 = 68;
    public static final short SYNC_2 = 181;

    /**
     *  闲置的CPU
     **/
    public static byte cpuiDle;
    /**
     * 消息ID
     **/
    private Short messageID;
    /**
     * 信息长度（字节）
     **/
    private Short messageLength;

    /**
     * 接收机工作的时间系统
     **/
    private byte TimeRef;

    /**
     *  GPS时间质量
     **/
    private Byte timeStatus;

    /**
     *  时间周
     **/
    private Short wn;

    /**
     * 以ms为单位的GPS周内秒
     **/
    private Integer ms;

    /**
     * 保留字段
     **/
    private Integer reserved;

    /**
     * 版本信息
     **/
    private Byte version;

    /**
     *
     **/
    private Short leapSec;

    /**
     * 最近一秒中两个具有相同信息ID的log之间的处理器空闲时间。
     **/
    private Byte delayMs;

}
