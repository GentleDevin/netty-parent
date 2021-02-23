package com.netty.chat.client.context;

import com.netty.chat.processor.MsgProcessor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Title:
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/2/4 15:08
 */
public class ApplicationContext {
    public static Map<Integer, ChannelHandlerContext> onlineUsers = new HashMap<Integer,ChannelHandlerContext>();
    public static Map<String, Channel> allChannels = new HashMap<String,Channel>();

    public static void add(Integer uid,ChannelHandlerContext ctx){
        onlineUsers.put(uid,ctx);
    }

    public static void remove(Integer uid){
        onlineUsers.remove(uid);
    }

    public static ChannelHandlerContext getContext(Integer uid){
        return onlineUsers.get(uid);
    }

    public static Map<String, Channel>  getAllChannels() {
        return allChannels;
    }

    public static void putChannel(Channel channel) {
        String channelId = channel.id().toString();
        allChannels.put(channelId,channel);
    }

    public  static String onlineUserInfos(ChannelGroup onlineUsers) {
        Iterator<Channel> iterator = onlineUsers.iterator();
        StringBuilder channelStr = new StringBuilder();
        while (iterator.hasNext()) {
            Channel channel = iterator.next();
            String id = channel.id().toString();
            String senderName =  new MsgProcessor().getSenderName(channel);
            channelStr.append(channel.id() + ":"+senderName+",");
        }
        return channelStr.toString();
    }


    public static String getAllChannel(ChannelGroup onlineUsers) {
        Iterator<Channel> iterator = onlineUsers.iterator();
        StringBuilder channelStr = new StringBuilder();
        while (iterator.hasNext()) {
          Channel channel = iterator.next();
          channelStr.append(channel.id() + ",");
        }
         return channelStr.toString();
    }

}
