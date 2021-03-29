package com.netty.gnss.common;

/**
 * @Title: 数据解析工具类
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/3/22 16:25
 */
public class DataParseUtil {
    
    /**
     * @Description: 根据卫星系统和卫星号获取卫星标识符
     * @CreateDate: 2021/03/29 09:52:15
     * @param prn: 
     * @param SateSys:
     * @return: java.lang.String
     **/
    public static String getSatelliteSystemPrn(short prn,byte SateSys) {
        String SatelliteSystemTag = SatelliteSystemEnum.getName(SateSys);
        return SatelliteSystemTag + prn;
    }
    
}
