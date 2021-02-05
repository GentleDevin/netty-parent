package com.netty.chat.client.context;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title:
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/2/4 15:08
 */
public class ApplicationContext {
    public static Map<Integer, ChannelHandlerContext> onlineUsers = new HashMap<Integer,ChannelHandlerContext>();
    public static void add(Integer uid,ChannelHandlerContext ctx){
        onlineUsers.put(uid,ctx);
    }

    public static void remove(Integer uid){
        onlineUsers.remove(uid);
    }

    public static ChannelHandlerContext getContext(Integer uid){
        return onlineUsers.get(uid);
    }
}
