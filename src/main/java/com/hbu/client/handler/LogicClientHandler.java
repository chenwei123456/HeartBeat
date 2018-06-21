package com.hbu.client.handler;

import com.hbu.common.protobuf.Command;
import com.hbu.common.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;

/**
 * @author chenwei
 * @date 2018/6/18
 */

public class LogicClientHandler extends SimpleChannelInboundHandler<Message.MessageBase>{
	public Logger log = Logger.getLogger(this.getClass());

	private final static String CLIENTID = "1234567890";

	// 连接成功后，向server发送消息
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Message.MessageBase.Builder authMsg = Message.MessageBase.newBuilder();
		authMsg.setClientId(CLIENTID);
		authMsg.setCmd(Command.CommandType.AUTH);
		authMsg.setData("This is auth data");

		ctx.writeAndFlush(authMsg.build());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.debug("连接断开 ");
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message.MessageBase msg) throws Exception {
		if(msg.getCmd().equals(Command.CommandType.AUTH_BACK)){
			log.debug("验证成功");
			ctx.writeAndFlush(
					Message.MessageBase.newBuilder()
							.setClientId(CLIENTID)
							.setCmd(Command.CommandType.PUSH_DATA)
							.setData("This is upload data")
							.build()
			);

		}else if(msg.getCmd().equals(Command.CommandType.PING)){
			//接收到server发送的ping指令
			log.info(msg.getData());

		}else if(msg.getCmd().equals(Command.CommandType.PONG)){
			//接收到server发送的pong指令
			log.info(msg.getData());

		}else if(msg.getCmd().equals(Command.CommandType.PUSH_DATA)){
			//接收到server推送数据
			log.info(msg.getData());

		}else if(msg.getCmd().equals(Command.CommandType.PUSH_DATA_BACK)){
			//接收到server返回数据
			log.info(msg.getData());

		}else{
			log.info(msg.getData());
		}
	}
}
