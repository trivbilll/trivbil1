/*
 * Copyright 2012 The Netty Project
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.example.echo;

import java.io.UnsupportedEncodingException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

/**
 * Handler implementation for the echo client. It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter
{
	
	private final ByteBuf	firstMessage;
	private static int		i	= 0;
	
	/**
	 * Creates a client-side handler.
	 */
	public EchoClientHandler()
	{
		firstMessage = Unpooled.buffer(EchoClient.SIZE);
		// for (int i = 0; i < firstMessage.capacity(); i++)
		// {
		// firstMessage.writeByte((byte) i);
		// }
		try
		{
			firstMessage.writeBytes(("hello message " + i++).getBytes("UTF-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception
	{
		final AttributeKey<String> id = AttributeKey.valueOf("id");
		// 通道注册后执行，获取属性值
		String idValue = ctx.channel().attr(id).get();
		System.out.println(idValue);
		// do something with the idValue
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx)
	{
		System.out.println(ctx.channel().localAddress() + " | " + i);
		ctx.writeAndFlush(firstMessage);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
	{
		ByteBuf result = (ByteBuf) msg;
		byte[] result1 = new byte[result.readableBytes()];
		result.getBytes(0, result1);
		String resultStr = new String(result1);
		System.out.println(" read :" + resultStr);
		// result.release();
		ctx.write(msg);
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx)
	{
		ctx.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}
}
