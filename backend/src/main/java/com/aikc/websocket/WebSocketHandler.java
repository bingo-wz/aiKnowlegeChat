package com.aikc.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 消息处理器
 * 处理聊天消息、协同编辑等实时通信
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final Logger log = LoggerFactory.getLogger(WebSocketHandler.class);

    /**
     * 用户ID -> Channel 映射
     */
    public static final ConcurrentHashMap<String, ChannelHandlerContext> USER_CHANNELS = new ConcurrentHashMap<>();

    /**
     * 课堂ID -> 用户ID集合 映射
     */
    public static final ConcurrentHashMap<String, ConcurrentHashMap<String, ChannelHandlerContext>> CLASSROOM_CHANNELS = new ConcurrentHashMap<>();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            log.info("WebSocket 连接建立: {}", ctx.channel().id());
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        String payload = msg.text();
        log.debug("收到消息: {}", payload);

        // 解析消息类型并处理
        // 消息格式: {"type":"CHAT","classroomId":"123","userId":"456","content":"hello"}
        handleMessage(ctx, payload);
    }

    /**
     * 处理收到的消息
     */
    private void handleMessage(ChannelHandlerContext ctx, String payload) {
        // TODO: 解析 JSON 并根据消息类型处理
        // 1. 聊天消息 - 广播给同课堂所有用户
        // 2. 协同编辑 - 使用 OT 算法转换后广播
        // 3. 用户加入/离开 - 更新在线列表
    }

    /**
     * 广播消息到指定课堂的所有用户
     */
    public static void broadcastToClassroom(String classroomId, String message) {
        ConcurrentHashMap<String, ChannelHandlerContext> channels = CLASSROOM_CHANNELS.get(classroomId);
        if (channels != null) {
            channels.values().forEach(channel -> {
                channel.writeAndFlush(new TextWebSocketFrame(message));
            });
        }
    }

    /**
     * 发送消息给指定用户
     */
    public static void sendToUser(String userId, String message) {
        ChannelHandlerContext channel = USER_CHANNELS.get(userId);
        if (channel != null) {
            channel.writeAndFlush(new TextWebSocketFrame(message));
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        log.info("WebSocket 连接断开: {}", ctx.channel().id());
        // 清理用户连接
        removeUserChannel(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("WebSocket 异常", cause);
        ctx.close();
    }

    /**
     * 移除用户连接
     */
    private void removeUserChannel(ChannelHandlerContext ctx) {
        USER_CHANNELS.entrySet().removeIf(entry -> entry.getValue() == ctx);
        CLASSROOM_CHANNELS.values().forEach(channels ->
            channels.entrySet().removeIf(entry -> entry.getValue() == ctx)
        );
    }
}
