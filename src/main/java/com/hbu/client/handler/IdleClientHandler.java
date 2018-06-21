package com.hbu.client.handler;

import com.hbu.client.NettyClient;
import com.hbu.common.protobuf.Command;
import com.hbu.common.protobuf.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * @author chenwei
 * @date 2018/6/19
 */
public class IdleClientHandler extends SimpleChannelInboundHandler<Message> {
	public Logger log = Logger.getLogger(this.getClass());

	private NettyClient nettyClient;
	private int heartbeatCount = 0;
	private final static String CLIENTID = "1234567890";

	/**
	 * @param nettyClient
	 */
	public IdleClientHandler(NettyClient nettyClient) {
		this.nettyClient = nettyClient;
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			String type = "";
			if (event.state() == IdleState.READER_IDLE) {
				type = "read idle";
			} else if (event.state() == IdleState.WRITER_IDLE) {
				type = "write idle";
			} else if (event.state() == IdleState.ALL_IDLE) {
				type = "all idle";
			}
			log.debug(ctx.channel().remoteAddress() + "保持心跳的类型：" + type);
			sendPingMsg(ctx);
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

	/**
	 * 发送ping消息
	 * @param context
	 */
	protected void sendPingMsg(ChannelHandlerContext context) {
		context.writeAndFlush(
				Message.MessageBase.newBuilder()
						.setClientId(CLIENTID)
						.setCmd(Command.CommandType.PING)
						.setData("This is a ping msg")
						.build()
		);
		heartbeatCount++;
		log.info("Client sent ping msg to " + context.channel().remoteAddress() + ", count: " + heartbeatCount);
	}

	/**
	 * 处理断开重连
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		final EventLoop eventLoop = ctx.channel().eventLoop();
		eventLoop.schedule(() -> nettyClient.doConnect(new Bootstrap(), eventLoop), 10L, TimeUnit.SECONDS);
		super.channelInactive(ctx);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

	}
}

