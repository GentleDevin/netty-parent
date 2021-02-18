package com.netty.chat.client.handler;

import com.netty.chat.client.context.ApplicationContext;
import com.netty.chat.processor.MsgProcessor;
import com.netty.chat.protocol.IMMessage;
import com.netty.chat.protocol.IMP;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import static com.netty.chat.processor.MsgProcessor.onlineUsers;

/**
 * @Title: 聊天客户端逻辑实现
 * @Description:
 * @Author: Devin
 * @CreateDate: 2021/02/03 15:53:31
 **/
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<IMMessage> {
	private MsgProcessor processor = new MsgProcessor();

	private ChannelHandlerContext ctx;
	private String nickName;
	public ClientHandler(String nickName){
		this.nickName = nickName;
	}
	private String receiver;

	/**启动客户端控制台*/
    private void session() throws IOException {
    		new Thread(){
    			@Override
				public void run(){
					System.out.println(nickName + ",您好，请在控制台输入对话内容...");
    				IMMessage message = null;
    		        Scanner scanner = new Scanner(System.in);
    		        do{
    			        	if(scanner.hasNext()){
    			        		String input = scanner.nextLine();
    			        		if("exit".equals(input)){
    			        			message = new IMMessage(IMP.LOGOUT.getName(),"Console",System.currentTimeMillis(),nickName);
    			        		}else if("1".equals(input)){
									System.out.println("----请输入聊天人ID----");
									String receiverName = scanner.nextLine();
									System.out.println("----请输入对话内容----");
									input = scanner.nextLine();
									message = new IMMessage(IMP.CHAT.getName(),System.currentTimeMillis(),nickName,receiverName,input);
    			        		}else{
    			        			message = new IMMessage(IMP.CHAT.getName(),System.currentTimeMillis(),nickName,"-1",input);
    			        		}
    			        	}
    		        }
    		        while (sendMsg(message));
    		        scanner.close();
    			}
    		}.start();
    }
	
    /**
	 * tcp链路建立成功后调用
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.ctx = ctx;
		IMMessage message = new IMMessage(IMP.LOGIN.getName(),"Console",System.currentTimeMillis(),this.nickName);
		sendMsg(message);
		initChatType();
		session();
	}
	/**
     * 发送消息
     * @param msg
     * @return
     * @throws IOException 
     */
    private boolean sendMsg(IMMessage msg){
        ctx.channel().writeAndFlush(msg);
		System.out.println("继续输入开始对话...");
        return msg.getCmd().equals(IMP.LOGOUT) ? false : true;
    }
	/**
	 * 收到消息后调用
	 * @throws IOException
	 */
	@Override
    public void channelRead0(ChannelHandlerContext ctx, IMMessage msg) throws IOException {
		IMMessage m = (IMMessage)msg;
		if(null != m.getCmd() && m.getCmd().equals(IMP.SYSTEM.getName()) && null!=  m.getContent()) {
			System.out.println(IMP.SYSTEM.getName() +":" +m.getContent());
		}else{
			System.out.println((null == m.getSender() ? "" : (m.getSender() + ":")) + m.getContent());
		}
    }


    /**
     * 发生异常时调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	log.info("与服务器断开连接:"+cause.getMessage());
		cause.printStackTrace();
        ctx.close();
    }

	public void initChatType() {
		System.out.println("----请选择聊天方式----");
		System.out.println("----1.个人聊天----");
		System.out.println("----2.群聊----");
	}
}
