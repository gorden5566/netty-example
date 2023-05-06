package com.gorden5566.example;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


public class TcpProxyInitializer extends ChannelInitializer<SocketChannel> {

    private final String remoteHost;
    private final int remotePort;
    private final boolean enableDebug;

    public TcpProxyInitializer(String remoteHost, int remotePort, boolean enableDebug) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.enableDebug = enableDebug;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        if (enableDebug) {
            ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
        }
        ch.pipeline().addLast(new TcpProxyFrontendHandler(remoteHost, remotePort));
    }
}
