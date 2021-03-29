package com.netty.io.aio.server;


import com.netty.io.aio.ChannelInitializer;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;


public class AioServerChannelInitializer extends ChannelInitializer  {

    @Override
    protected void initChannel(AsynchronousSocketChannel channel) throws Exception {
        channel.read(ByteBuffer.allocate(1024), 10, TimeUnit.SECONDS, null, new AioServerHandler(channel, Charset.forName("UTF-8")));
    }

}
