package com.netty.gnss.processor;

import io.netty.channel.Channel;

import java.io.*;

/***
 * @Title:	客户端消息处理器
 * @Description:
 * @Author: Devin 
 * @CreateDate: 2021/02/24 16:48:47
 **/
public class ClientMsgProcessor {


	/**
	 * @Description: 读取二进制文件内容
	 * @CreateDate: 2021/03/05 15:09:11
	 * @param channel:
	 * @return: void
	 **/
    public void initMsgData(Channel channel){
        String filePath = "C:\\Users\\gentl\\Desktop\\Todo\\WorkPlan\\GNSS\\WFSXJ9-G9-R-2020-10-23-06";
		InputStream in = null;
		BufferedInputStream buffer = null;
		DataInputStream dataIn = null;
		try {
			in = new FileInputStream(filePath);
			buffer = new BufferedInputStream(in);
			dataIn = new DataInputStream(buffer);
			byte[] buf = new byte[1024];
			int byteReader = 0;
			int byteReaderCount = 0;
			while((byteReader = dataIn.read(buf)) != -1){
				byteReaderCount = byteReaderCount + byteReader;
				channel.writeAndFlush(buf);
				System.out.println("byteReader=" + byteReaderCount);
			}
        } catch (IOException  e) {
            e.printStackTrace();
        }
    }

	/**
	 * 发送消息
	 * @param
	 */
	public void sendMsg(Channel channel){
		initMsgData(channel);
	}
	
}
