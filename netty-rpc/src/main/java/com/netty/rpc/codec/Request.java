package com.netty.rpc.codec;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title:
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/1/28 15:41
 */
@NoArgsConstructor
@Data
public class Request {
    private String requestId;
    private Object parameter;

}
