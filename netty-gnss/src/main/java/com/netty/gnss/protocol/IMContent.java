package com.netty.gnss.protocol;

import com.netty.gnss.protocol.header.IMHeader;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Title: 消息内容
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/3/5 15:01
 */

@NoArgsConstructor
@Data
public class IMContent  {

    private Integer obs;
    private List imData;

}
