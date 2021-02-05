package com.netty.chat.client.handler;

import com.netty.chat.protocol.IMMessage;
import com.netty.chat.protocol.IMP;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Scanner;

/**
 * @Title: 聊天客户端逻辑实现
 * @Description:
 * @Author: Devin
 * @CreateDate: 2021/02/03 15:53:31
 **/
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<IMMessage> {

	private ChannelHandlerContext ctx;
	private String nickName;
	public ClientHandler(String nickName){
		this.nickName = nickName;
	}
	
	/**启动客户端控制台*/
    private void session() throws IOException {
    		new Thread(){
    			public void run(){
					System.out.println(nickName + ",您好，请在控制台输入对话内容...");
    				IMMessage message = null;
    		        Scanner scanner = new Scanner(System.in);
    		        do{
    			        	if(scanner.hasNext()){
    			        		String input = scanner.nextLine();
    			        		if("exit".equals(input)){
    			        			message = new IMMessage(IMP.LOGOUT.getName(),"Console",System.currentTimeMillis(),nickName);
    			        		}else{
    			        			message = new IMMessage(IMP.CHAT.getName(),System.currentTimeMillis(),nickName,"seussie",input);
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
		System.out.println((null == m.getSender() ? "" : (m.getSender() + ":")) + m.getContent());
    }


    /**
     * 发生异常时调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	log.info("与服务器断开连接:"+cause.getMessage());
        ctx.close();
    }
}
