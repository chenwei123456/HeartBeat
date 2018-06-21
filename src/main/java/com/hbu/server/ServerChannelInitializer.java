package com.hbu.server;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.hbu.common.protobuf.Message;
import com.hbu.server.handler.IdleServerHandler;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;


/**
 * @author chenwei
 * @date 2018/6/20
 */

@Component
@Qualifier("serverChannelInitializer")
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
	//读操作空闲5秒
	private final static int READER_IDLE_TIME_SECONDS = 5;
	//写操作空闲5秒
	private final static int WRITER_IDLE_TIME_SECONDS = 5;
	//读写全部空闲10秒
	private final static int ALL_IDLE_TIME_SECONDS = 10;
	@Autowired
	@Qualifier("authServerHandler")
	private ChannelInboundHandlerAdapter authServerHandler;
	@Autowired
	@Qualifier("logicServerHandler")
	private ChannelInboundHandlerAdapter logicServerHandler;

	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		ChannelPipeline p = socketChannel.pipeline();

		p.addLast("idleStateHandler", new IdleStateHandler(READER_IDLE_TIME_SECONDS, WRITER_IDLE_TIME_SECONDS, ALL_IDLE_TIME_SECONDS, TimeUnit.SECONDS));
		p.addLast("idleTimeoutHandler", new IdleServerHandler());

		p.addLast(new ProtobufVarint32FrameDecoder());
		p.addLast(new ProtobufDecoder(Message.MessageBase.getDefaultInstance()));

		p.addLast(new ProtobufVarint32LengthFieldPrepender());
		p.addLast(new ProtobufEncoder());

		p.addLast("authServerHandler", authServerHandler);
		p.addLast("hearableServerHandler", logicServerHandler);
	}
}