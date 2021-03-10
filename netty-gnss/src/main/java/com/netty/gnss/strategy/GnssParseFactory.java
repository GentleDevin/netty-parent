package com.netty.gnss.strategy;

import com.netty.gnss.common.ParseKey;
import com.netty.gnss.strategy.impl.RangeCmpParse;
import com.netty.gnss.strategy.impl.RangeParse;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Title: 解析工厂
 * @Description: 生产GNSS不同解析数据key
 * @Author: Devin
 * CreateDate: 2021/3/5 14:20
 */
public class GnssParseFactory {

    private static Map<Short, IGnssParse> gnssParseMap = new HashMap<>();

    static {
        gnssParseMap.put(ParseKey.MSG_ID_43.getKey(),new RangeParse());
        gnssParseMap.put(ParseKey.MSG_ID_140.getKey(),new RangeCmpParse());
    }

    public static IGnssParse getGnssParseStrategy(Short ParseKey)  {
        return gnssParseMap.get(ParseKey);
    }

    public static Set<Short> getParseKeys(Integer ParseKey)  {
        return  gnssParseMap.keySet();
    }

}
