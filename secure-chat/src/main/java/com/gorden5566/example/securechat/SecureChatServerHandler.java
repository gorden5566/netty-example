package com.gorden5566.example.securechat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles a server-side channel.
 */
public class SecureChatServerHandler extends SimpleChannelInboundHandler<String> {

    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    static final Map<Channel, String> channelNames = new ConcurrentHashMap<>();

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        // Once session is secured, send a greeting and register the channel to the global channel
        // list so the channel received the messages from others.
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
            new GenericFutureListener<Future<Channel>>() {
                @Override
                public void operationComplete(Future<Channel> future) throws Exception {
                    ctx.writeAndFlush(
                        "Welcome to " + InetAddress.getLocalHost().getHostName() + " secure chat service!\n");
                    ctx.writeAndFlush(
                        "Your session is protected by " +
                            ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() +
                            " cipher suite.\n");
                    ctx.writeAndFlush("Please set your name, here is an example: name=Axe\n");

                    channels.add(ctx.channel());
                    System.out.println("[" + getChannelName(ctx.channel()) + "] join in chat room");
                }
            });
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        if (msg.startsWith("name=")) {
            String channelName = getPropertyVal(msg);
            if (channelName == null) {
                ctx.writeAndFlush("name cannot be null\n");
            } else {
                changeName(ctx, channelName);
            }
            return;
        }

        // Send the received message to all channels but the current one.
        for (Channel c: channels) {
            if (c != ctx.channel()) {
                c.writeAndFlush("[" + getChannelName(ctx.channel()) + "] " + msg + '\n');
            } else {
                c.writeAndFlush("[you] " + msg + '\n');
            }
        }

        // Close the connection if the client has sent 'bye'.
        if ("bye".equals(msg.toLowerCase())) {
            ctx.close();
        }
    }

    private void changeName(ChannelHandlerContext ctx, String channelName) {
        System.out.println("[" + getChannelName(ctx.channel()) + "] change name to: " + channelName + "\n");
        channelNames.put(ctx.channel(), channelName + "@" + ctx.channel().remoteAddress());
        ctx.writeAndFlush("you name is " + channelName + "\n");
    }

    private String getPropertyVal(String msg) {
        String[] split = msg.split("=");
        if (split.length < 2) {
            return null;
        }
        return split[1];
    }

    private String getChannelName(Channel channel) {
        String name = channelNames.get(channel);
        return name != null ? name : channel.remoteAddress().toString();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
