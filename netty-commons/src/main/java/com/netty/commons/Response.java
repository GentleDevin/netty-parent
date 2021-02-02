package com.netty.commons;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Title:
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/1/28 15:44
 */
@Data
@NoArgsConstructor
public class Response {
    private String requestId;
    private Object result;
}
