package com.netty.gnss.strategy.impl;

import com.netty.gnss.protocol.IMMessage;
import com.netty.gnss.protocol.content.Range;
import com.netty.gnss.strategy.IGnssParse;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: Range数据解析
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/3/5 14:14
 */
public class RangeParse implements IGnssParse {
    private int msgCount;

    @Override
    public void gnssParse(ByteBuf in, IMMessage imMessage) {
        List<Range> imContentList = new ArrayList<>();

        //CRC校验正确，开始解析数据,ridx = 32
        for (int readContentIndex = 0; readContentIndex < imMessage.getImContent().getObs(); readContentIndex++) {
            Range range = new Range();
            range.setPrnOrSlot(in.readShortLE());
            range.setGlofreq(in.readShortLE());
            range.setPsr(in.readDoubleLE());
            range.setPsrStd(in.readFloatLE());
            range.setAdr(in.readDoubleLE());
            range.setAdrStd(in.readFloatLE());
            range.setDopp(in.readFloatLE());
            range.setCOrNo(in.readFloatLE());
            range.setLocktime(in.readFloatLE());
            range.setChTrStatus(in.readIntLE());
            imContentList.add(range);
        }
            //报文数据已解析完成
            imMessage.setHeaderMatch(false);
            imMessage.getImContent().setImData(imContentList);
            in.skipBytes(4);
            System.out.println("msgCount= " + ++msgCount);
    }
}
