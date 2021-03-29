package com.netty.gnss.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Title: 卫星系统号对应的标识符
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/3/26 16:52
 */

@Getter
@AllArgsConstructor
public enum SatelliteSystemEnum {

    /**
     * GPS
     **/
    GPS("G",0),

    /**
     * GLONASS
     **/
    GLONASS("R",1),

    /**
     * SBAS
     **/
    SBAS("S",2),

    /**
     * GAL
     **/
    GAL("E",3),

    /**
     * BDS
     **/
    BDS("C",4),

    /**
     * QZSS
     **/
    QZSS("J",5);

    private String key;
    private int value;


    public static String getName(int value) {
        for (SatelliteSystemEnum tag : SatelliteSystemEnum.values()) {
            if(tag.getValue() == value)  {
                return tag.getKey();
            }
        }
        return null;
    }
}
