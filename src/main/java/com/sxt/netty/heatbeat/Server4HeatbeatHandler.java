package com.sxt.netty.heatbeat;

import com.sxt.utils.HeatbeatMessage;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

@Sharable
public class Server4HeatbeatHandler extends ChannelHandlerAdapter {

    private static List<String> credentials = new ArrayList<>();
    private static final String HEATBEAT_SUCCESS = "SERVER_RETURN_HEATBEAT_SUCCESS";

    /**
     * 初始化客户端列表信息。一般通过配置文件读取或数据库读取
     */
    public Server4HeatbeatHandler() {
        credentials.add("192.168.229.1_DESKTOP-1TH8I86");
    }

    /**
     * 业务处理逻辑
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 身份校验
        if (msg instanceof String) {
            this.checkCredential(ctx, msg.toString());
        }
        // 读取客户端发来系统信息
        else if (msg instanceof HeatbeatMessage) {
            this.readHeatbeatMessage(ctx, msg);
        }
        // 错误的系统消息
        else {
            ctx.writeAndFlush("wrong message").addListener(ChannelFutureListener.CLOSE);
        }

    }

    /**
     * 身份检查。检查客户端身份是否有效。
     * 客户端身份信息应该是通过数据库或数据文件定制的。
     * 身份通过 - 返回确认消息。
     * 身份无效 - 断开连接
     */
    private void checkCredential(ChannelHandlerContext ctx, String credential) {
        System.out.println(credential);
        System.out.println(credentials);
        if (credentials.contains(credential)) {
            ctx.writeAndFlush(HEATBEAT_SUCCESS);
        } else {
            ctx.writeAndFlush("没有权限链接服务器").addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 接受客户端发来的系统信息
     */
    private void readHeatbeatMessage(ChannelHandlerContext ctx, Object msg) {
        HeatbeatMessage message = (HeatbeatMessage) msg;
        System.out.println(message);
        ctx.writeAndFlush("SERVER_RETURN_HEATBEAT_SUCCESS");
    }



    /**
     * 异常处理逻辑
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("server exceptionCaught method run...");
        ctx.close();
    }

}
