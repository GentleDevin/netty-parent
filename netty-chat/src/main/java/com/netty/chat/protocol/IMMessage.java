package com.netty.chat.protocol;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.msgpack.annotation.Message;

import java.util.Map;


/**
 * 自定义消息实体类
 *
 */
@Message
@NoArgsConstructor
@Data
public class IMMessage{
	
	private String addr;		//IP地址及端口
	private String cmd;		//命令类型[LOGIN]或者[SYSTEM]或者[LOGOUT]
	private long time;		//命令发送时间
	private int online;		//当前在线人数
	private String sender;  //发送人
	private String receiver;	//接收人
	private String content;		//消息内容
	private String terminal; 	//终端
	private Map<String, Channel> allChannels;
	private String onlineUserInfos;


	public IMMessage(String cmd,long time,int online,String content){
    	this.time = time;
		this.online = online;
		this.content = content;
		this.terminal = terminal;
	}

	public IMMessage(String cmd,String terminal,long time,String sender){
		this.cmd = cmd;
		this.time = time;
		this.sender = sender;
		this.terminal = terminal;
	}

	public IMMessage(String cmd,long time,String sender,String receiver,String content){
		this.cmd = cmd;
		this.time = time;
		this.sender = sender;
		this.receiver = receiver;
		this.content = content;
	}

	public IMMessage(String cmd,long time,String sender,String receiver,String content, Map<String, Channel> allChannels){
		this.cmd = cmd;
		this.time = time;
		this.sender = sender;
		this.receiver = receiver;
		this.content = content;
		this.allChannels = allChannels;
	}

	public IMMessage(String cmd,long time,String sender,String receiver,String content,String onlineUserInfos){
		this.cmd = cmd;
		this.time = time;
		this.sender = sender;
		this.receiver = receiver;
		this.content = content;
		this.onlineUserInfos = onlineUserInfos;
	}

	public IMMessage(String cmd,long time,String sender,String content){
		this.cmd = cmd;
		this.time = time;
		this.sender = sender;
		this.content = content;
		this.terminal = terminal;
	}



}
