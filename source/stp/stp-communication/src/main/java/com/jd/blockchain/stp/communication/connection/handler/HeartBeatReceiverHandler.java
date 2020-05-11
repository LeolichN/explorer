/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: com.jd.blockchain.stp.communication.connection.handler.HeartBeatSenderHandler
 * Author: shaozhuguang
 * Department: Jingdong Digits Technology
 * Date: 2019/4/15 上午10:10
 * Description:
 */
package com.jd.blockchain.stp.communication.connection.handler;

import com.jd.blockchain.stp.communication.message.HeartBeatMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 心跳接收Handler
 * @author shaozhuguang
 * @create 2019/4/15
 * @since 1.0.0
 */
@ChannelHandler.Sharable
public class HeartBeatReceiverHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 判断当前收到的信息是否为心跳信息
        if (HeartBeatMessage.isHeartBeat(msg)) {
            // 收到的消息是心跳消息，此时需要回复一个心跳消息
            HeartBeatMessage.write(ctx);
            System.out.println("Receive HeartBeat Request Message -> " + msg.toString());
        } else {
            // 非心跳信息的情况下交由其他Handler继续处理
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 出现异常直接关闭连接
        ctx.close();
    }
}