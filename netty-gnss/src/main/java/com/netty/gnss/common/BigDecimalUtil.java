package com.netty.gnss.common;

import java.math.BigDecimal;

/**
 * @Title: BigDecimal工具类
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/3/22 16:25
 */
public class BigDecimalUtil {

    /**
     * @Description: BigDecimal保留小数点后两位
     * @CreateDate: 2021/03/22 16:26:42
     * @param data:
     * @return: java.math.BigDecimal
     **/
    public static BigDecimal getBigDecimal(double data)  {
        BigDecimal bigDecimal = new BigDecimal(data);
        //保留两位小数且四舍五入
        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}
