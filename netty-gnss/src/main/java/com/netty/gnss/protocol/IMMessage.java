package com.netty.gnss.protocol;

import com.netty.gnss.protocol.header.IMHeader;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Title: 自定义消息实体类
 * @Description:
 * @Author: Devin
 * @CreateDate: 2021/02/26 11:07:01
 **/
@NoArgsConstructor
@Data
public class IMMessage {
	/**
	 *  头部信息
	 **/
	private IMHeader imHeader;

	/**
	 *  消息内容
	 **/
	private IMContent imContent;

	/**
	 *  CRC校验值
	 **/
	private int crc;

	/**
	 *  字节数据
	 **/
	private byte[] imBytes;

	/**
	 *	消息总长度，不包括CRC32位长度
	 **/
	private int msgLength ;

	/**
	 * 数据是否可读
	 **/
	private boolean isReadable = true;


	/**
	 *  头是否匹配
	 **/
	private	boolean isHeaderMatch = false;

}
