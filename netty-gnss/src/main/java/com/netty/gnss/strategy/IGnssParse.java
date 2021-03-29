package com.netty.gnss.strategy;

import com.netty.gnss.protocol.IMMessage;
import io.netty.buffer.ByteBuf;

/**
 * @Title: Gnss解析接口
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/3/5 14:07
 */
public interface IGnssParse {

    /**
     * @Description:  数据解析
     * @CreateDate: 2021/03/29 09:11:58
     * @param in:
     * @param imMessage:
     * @return: void
     **/
    void gnssParse(ByteBuf in, IMMessage imMessage);

}
