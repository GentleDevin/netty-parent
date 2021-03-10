package com.netty.gnss.processor;

import com.netty.gnss.common.CrcValidate;
import com.netty.gnss.common.ParseKey;
import com.netty.gnss.protocol.IMContent;
import com.netty.gnss.protocol.IMMessage;
import com.netty.gnss.protocol.header.IMHeader;
import com.netty.gnss.strategy.GnssParseFactory;
import com.netty.gnss.strategy.IGnssParse;
import io.netty.buffer.ByteBuf;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/***
 * @Title: 服务端消息处理器
 * @Description:
 * @Author: Devin
 * @CreateDate: 2021/02/24 16:48:47
 **/
public class ServerMsgProcessor {
	//固定的基本长度
	private final int BASE_LENGTH = 6;
	//记录包头开始的index
	private int beginReader = 0;
	//匹配头数量
	private int countHeader;
	//初始化GNSS解析信息ID
	static Map<Short, String> msgIdMap = null;
    //GNSS报文消息类
	private IMMessage imMessage = new IMMessage();

	//字节数组CRC校验
	private byte[] bytesCrc = null;
	//CRC校验失败统计
	private int countCrcFailure;

	static {
		msgIdMap = new HashMap();
		/*msgIdMap.put(ParseKey.MSG_ID_43.getKey(),ParseKey.MSG_ID_43.getValue());*/
		msgIdMap.put(ParseKey.MSG_ID_140.getKey(),ParseKey.MSG_ID_140.getValue());
	}

	/**
	 * @Description: 解析GNSS数据
	 * @CreateDate: 2021/03/10 11:25:01
	 * @param in: 
	 * @return: com.netty.gnss.protocol.IMMessage
	 **/
	public IMMessage parseGnss(ByteBuf in){
		// 开始匹配头部信息
		if(!imMessage.isHeaderMatch()) {
			imMessage = matchHeader(in, msgIdMap);
		}

		//头部信息匹配成功
		if(imMessage.isHeaderMatch()) {
			if (crcValidate(in)) {
				IGnssParse gnssParseStrategy = GnssParseFactory.getGnssParseStrategy(ParseKey.MSG_ID_140.getKey());
				gnssParseStrategy.gnssParse(in,imMessage);
			}
		}
		return imMessage;
	}

	/**
	 * @Description: 匹配头部信息
	 * @CreateDate: 2021/03/10 11:25:37
	 * @param in:
	 * @param msgMap:
	 * @return: com.netty.gnss.protocol.IMMessage
	 **/
	public IMMessage matchHeader(ByteBuf in, Map<Short, String> msgMap) {
		while (true) {
			// 获取报文开始的index
			beginReader = in.readerIndex();
			// 报文开始标志匹配，结束while循环
			if (in.readByte() == IMHeader.SYNC_0) {
				if (in.readByte() == IMHeader.SYNC_1) {
					if (in.readByte() == IMHeader.SYNC_2) {
						Byte headerLength = in.readByte();
						Short messageID = in.readShortLE();
						if(!msgMap.isEmpty()) {
							if (msgMap.containsKey(messageID)) {
								return parseHeader(in, headerLength, messageID);
							}
						} else{
							return parseHeader(in, headerLength, messageID);
						}
					}
				}
			}
			// 可读数据不够基本数据长度，等待后面缓冲区数据到达
			if (in.readableBytes() < BASE_LENGTH) {
				imMessage.setReadable(false);
				break;
			}
		}
		return imMessage;
	}

	/**
	 * @Description: 解析头部信息
	 * @CreateDate: 2021/03/10 11:34:13
	 * @param in:
	 * @param headerLength:
	 * @param messageID:
	 * @return: com.netty.gnss.protocol.IMMessage
	 **/
	public IMMessage parseHeader(ByteBuf in, Byte headerLength, Short messageID){
		imMessage = new IMMessage();
		//头部信息类
		IMHeader imHeader = new IMHeader();
		imHeader.setHeaderLength(headerLength);
		imHeader.setMessageID(messageID);
		imHeader.setMessageType(in.readByte());
		in.skipBytes(1);
		imHeader.setMessageLength(in.readShortLE());
		in.skipBytes(2);
		imHeader.setIdleTime(in.readByte());
		imHeader.setTimeStatus(in.readByte());
		imHeader.setWeek(in.readShortLE());
		imHeader.setMs(in.readIntLE());
		in.skipBytes(4);
		imHeader.setBdsToGpsTime(in.readShortLE());
		in.skipBytes(2);
		imMessage.setImHeader(imHeader);
		//消息内容类
		IMContent imContent = new IMContent();
		imContent.setObs(in.readIntLE());
		imMessage.setImContent(imContent);

		// 标记报文数据开始的index
		in.markReaderIndex();
		imMessage.setHeaderMatch(true);
		System.out.println("match head=" + ++countHeader);
		return imMessage;
	}

	/**
	 * @Description: 提前CRC校验，防止丢包数据不全
	 * @CreateDate: 2021/03/10 11:26:39
	 * @param in:
	 * @return: boolean
	 **/
	public boolean crcValidate(ByteBuf in) {
		//消息总长度，不包括CRC32位长度
		int msgLength = imMessage.getImHeader().getHeaderLength() + imMessage.getImHeader().getMessageLength();
		imMessage.setMsgLength(msgLength);
		//可读数据长度不够，等待缓冲区数据过来
		if(in.readableBytes()  < msgLength)  {
			imMessage.setReadable(false);
			return false;
		}else{
			imMessage.setReadable(true);
		}

		//提前跳到CRC校验的位置
		in.skipBytes(imMessage.getImHeader().getMessageLength() - 4);

		//读取发送过来的CRC值
		imMessage.setCrc(in.readIntLE());
		bytesCrc = new byte[msgLength];
		//获取发送的二进制数据
		in.getBytes(beginReader,bytesCrc);
		imMessage.setImBytes(bytesCrc);
		//通过公式计算发送的二进制数据CRC值
		long readBytesCrc = CrcValidate.crc32(bytesCrc, bytesCrc.length);
		boolean crcValidateResult = imMessage.getCrc() == readBytesCrc;

		//CRC校验失败，直接丢弃整包数据
		if(!crcValidateResult) {
			//Todo 优化跳过丢包数据
			//进入下一次报文头部匹配
			imMessage.setHeaderMatch(false);
			System.out.println("countCrcFailure="+ ++countCrcFailure);
			return false;
		}

		//保存每个报文的二进制文件
		/*saveGnssFile(in,beginReader,msgLength);*/
		//重置当前读索引到数据解析前读索引
		in.resetReaderIndex();
		return true;
	}

	/**
	 * @Description: 将每个报文保存成一个二进制文件
	 * @CreateDate: 2021/03/05 15:11:01
	 * @param in:
	 * @param beginReader:
	 * @param msgLength:
	 * @return: void
	 **/
	int num = 0;
	public void saveGnssFile(ByteBuf in, int beginReader, int msgLength){
			byte[] bytes = new byte[msgLength+4];
			in.getBytes(beginReader,bytes);
			String filePath = "C:\\Users\\gentl\\Desktop\\Todo\\WorkPlan\\GNSS\\GNSS_FILE\\GNSS_"+ ++num;
			OutputStream os = null;
			DataOutputStream dataOs = null;
			try {
				os = new FileOutputStream (filePath);
				dataOs = new DataOutputStream(os);
				dataOs.write(bytes);
		} catch (IOException  e) {
			e.printStackTrace();
		}
	}



	public void readByteByNum(ByteBuf in,byte num){
		byte data = 0;
		for (int i = 0; i < num; i++) {
			data +=  in.readByte();
		}
	}


}
