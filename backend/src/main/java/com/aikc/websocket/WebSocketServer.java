package com.aikc.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Netty WebSocket 服务器
 * 用于实时聊天和协同编辑
 */
@Component
public class WebSocketServer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    @Value("${websocket.port:9090}")
    private int port;

    @Value("${websocket.path:/ws}")
    private String path;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    /**
     * 启动 WebSocket 服务器
     */
    public void start() {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            // HTTP 编解码
                            pipeline.addLast(new HttpServerCodec());
                            // HTTP 对象聚合
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            // 支持 WebSocket 数据压缩
                            pipeline.addLast(new WebSocketServerCompressionHandler());
                            // 分块写入处理
                            pipeline.addLast(new ChunkedWriteHandler());
                            // WebSocket 协议处理
                            pipeline.addLast(new WebSocketServerProtocolHandler(path));
                            // 自定义处理器
                            pipeline.addLast(new WebSocketHandler());
                        }
                    });

            ChannelFuture future = bootstrap.bind(port).sync();
            serverChannel = future.channel();
            log.info("WebSocket 服务器启动成功，端口: {}, 路径: {}", port, path);

        } catch (InterruptedException e) {
            log.error("WebSocket 服务器启动失败", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 停止 WebSocket 服务器
     */
    @PreDestroy
    public void stop() {
        if (serverChannel != null) {
            serverChannel.close();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        log.info("WebSocket 服务器已停止");
    }
}
