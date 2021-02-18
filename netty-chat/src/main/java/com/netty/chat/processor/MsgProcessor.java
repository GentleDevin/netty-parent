package com.netty.chat.processor;

import com.alibaba.fastjson.JSONObject;
import com.netty.chat.client.context.ApplicationContext;
import com.netty.chat.protocol.IMDecoder;
import com.netty.chat.protocol.IMEncoder;
import com.netty.chat.protocol.IMMessage;
import com.netty.chat.protocol.IMP;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Iterator;
import java.util.Map;

/**
 * @Title: 主要用于自定义协议内容的逻辑处理
 * @Description:
 * @Author: Devin
 * @CreateDate: 2021/02/03 14:13:19
 **/
public class MsgProcessor {
	
	//记录在线用户信息，存放一个个Channel，也可以建立map结构模拟不同的消息群
	public static ChannelGroup onlineUsers = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	//定义一些扩展属性
	public static final AttributeKey<String> SENDER_NAME = AttributeKey.valueOf("senderName");
	public static final AttributeKey<String> RECEIVER_NAME = AttributeKey.valueOf("receiverName");
	public static final AttributeKey<String> IP_ADDR = AttributeKey.valueOf("ipAddr");
	public static final AttributeKey<JSONObject> ATTRS = AttributeKey.valueOf("attrs");
	public static final AttributeKey<String> FROM = AttributeKey.valueOf("from");
	
	//自定义解码器
	private IMDecoder decoder = new IMDecoder();
	//自定义编码器
	private IMEncoder encoder = new IMEncoder();
	
	/**
	 * 获取发送者名称
	 * @param client
	 * @return
	 */
	public String getSenderName(Channel client){
		return client.attr(SENDER_NAME).get();
	}

	/**
	 * 获取接收者名称
	 * @param client
	 * @return
	 */
	public String getReceiverName(Channel client){
		return client.attr(RECEIVER_NAME).get();
	}

	/**
	 * 获取用户远程IP地址
	 * @param client
	 * @return
	 */
	public String getAddress(Channel client){
		return client.remoteAddress().toString().replaceFirst("/","");
	}
	
	/**
	 * 获取扩展属性
	 * @param client
	 * @return
	 */
	public JSONObject getAttrs(Channel client){
		try{
			return client.attr(ATTRS).get();
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * 获取扩展属性
	 * @param client
	 * @return
	 */
	private void setAttrs(Channel client, String key, Object value){
		try{
			JSONObject json = client.attr(ATTRS).get();
			json.put(key, value);
			client.attr(ATTRS).set(json);
		}catch(Exception e){
			JSONObject json = new JSONObject();
			json.put(key, value);
			client.attr(ATTRS).set(json);
		}
	}

	/**
	 * 获取所有在线用户名称
	 * @param
	 * @return
	 */
	public String getAllUserNames(ChannelGroup onlineUsers){
		StringBuilder channelStr = new StringBuilder();
		for (Channel channel : onlineUsers) {
			String senderName = getSenderName(channel);
			channelStr.append(senderName + ",");
		}
		return channelStr.toString();
	}

	/**
	 * 登出通知
	 * @param client
	 */
	public void logout(Channel client){
		//如果SenderName为null，没有遵从聊天协议的连接，表示未非法登录
		if(getSenderName(client) == null){ return; }
		for (Channel channel : onlineUsers) {
			IMMessage request = new IMMessage(IMP.SYSTEM.getName(), sysTime(), onlineUsers.size(), getSenderName(client) + "离开");
			String content = encoder.encode(request);
			channel.writeAndFlush(new TextWebSocketFrame(content));
		}
		onlineUsers.remove(client);
	}
	
	/**
	 * 发送消息
	 * @param client
	 * @param msg
	 */
	public void sendMsg(Channel client, IMMessage msg){
		sendMsg(client,encoder.encode(msg));
	}

	/**
	 * 发送消息
	 * @param client
	 * @param msg
	 */
	public void sendMsg(Channel client, String msg){
		IMMessage request = decoder.decode(msg);
		if(null == request){ return; }

		String addr = getAddress(client);
		//登录指令
		if(request.getCmd().equals(IMP.LOGIN.getName())){
			client.attr(SENDER_NAME).getAndSet(request.getSender());
			client.attr(RECEIVER_NAME).getAndSet(request.getReceiver());
			client.attr(IP_ADDR).getAndSet(addr);
			client.attr(FROM).getAndSet(request.getTerminal());
			onlineUsers.add(client);
			ApplicationContext.putChannel(client);
			for (Channel channel : onlineUsers) {
				boolean isself = (channel == client);
				if(!isself){
					request = new IMMessage(IMP.SYSTEM.getName(), sysTime(), onlineUsers.size(), getSenderName(client) + "加入");
				}else{
					request = new IMMessage(IMP.SYSTEM.getName(), sysTime(), getSenderName(client),getReceiverName(client),"在线用户信息："+ApplicationContext.onlineUserInfos(onlineUsers));
				}
				if("Console".equals(channel.attr(FROM).get())){
					channel.writeAndFlush(request);
					continue;
				}
			}
		}else if(request.getCmd().equals(IMP.CHAT.getName())){
			if(request.getReceiver().equals("-1")) {
					for (Channel channel : onlineUsers) {
						boolean isself = (channel == client);
						request.setCmd(IMP.CHAT.getName());
						request.setSender(getSenderName(client));
						request.setTime(sysTime());
						if("Console".equals(channel.attr(FROM).get()) & !isself){
							channel.writeAndFlush(request);
							continue;
						}
					}
				}else{
				if( null != request.getReceiver()) {
					Map<String, Channel> allChannels = ApplicationContext.getAllChannels();
					Channel channel = allChannels.get(request.getReceiver());
					channel.writeAndFlush(request);
				}
			}
		}
	}
	
	/**
	 * 获取系统时间
	 * @return
	 */
	private Long sysTime(){
		return System.currentTimeMillis();
	}
	
}
