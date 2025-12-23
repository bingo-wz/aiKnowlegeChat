package com.aikc.config;

import com.aikc.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * Netty 配置 - 应用启动后自动启动 WebSocket 服务器
 */
@Configuration
public class NettyConfig {

    @Autowired
    private WebSocketServer webSocketServer;

    @EventListener(ApplicationReadyEvent.class)
    public void startWebSocketServer() {
        // 在新线程中启动 WebSocket 服务器
        new Thread(() -> webSocketServer.start()).start();
    }
}
