package com.aikc.service;

import com.aikc.dto.ChatMessageDTO;
import com.aikc.entity.ChatMessage;
import com.aikc.entity.User;
import com.aikc.mapper.ChatMessageMapper;
import com.aikc.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天服务
 */
@Service
public class ChatService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 课堂ID -> 在线用户 (userId -> session)
     */
    private static final Map<Long, Map<Long, WebSocketSession>> CLASSROOM_SESSIONS = new ConcurrentHashMap<>();

    /**
     * 用户ID -> 当前所在课堂ID
     */
    private static final Map<Long, Long> USER_CLASSROOM = new ConcurrentHashMap<>();

    /**
     * 发送消息
     */
    public void sendMessage(ChatMessageDTO dto, Long userId) throws Exception {
        // 获取用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 保存消息到数据库
        ChatMessage message = new ChatMessage();
        message.setClassroomId(dto.getClassroomId());
        message.setUserId(userId);
        message.setUsername(user.getNickname());
        message.setAvatar(user.getAvatar());
        message.setMessageType(dto.getMessageType());
        message.setContent(dto.getContent());
        message.setFileUrl(dto.getFileUrl());
        message.setFileName(dto.getFileName());
        chatMessageMapper.insert(message);

        // 构建广播消息
        Map<String, Object> broadcast = new HashMap<>();
        broadcast.put("type", "message");
        broadcast.put("data", message);

        String payload = objectMapper.writeValueAsString(broadcast);

        // 广播给课堂内所有用户
        broadcastToClassroom(dto.getClassroomId(), payload);

        // 更新 Redis 中的最新消息
        String redisKey = "classroom:" + dto.getClassroomId() + ":messages";
        redisTemplate.opsForList().leftPush(redisKey, payload);
        redisTemplate.opsForList().trim(redisKey, 0, 99); // 保留最新100条
    }

    /**
     * 获取聊天历史 (分页)
     */
    public IPage<ChatMessage> getChatHistory(Long classroomId, int page, int size) {
        Page<ChatMessage> pageParam = new Page<>(page, size);
        return chatMessageMapper.selectPage(pageParam,
                new LambdaQueryWrapper<ChatMessage>()
                        .eq(ChatMessage::getClassroomId, classroomId)
                        .orderByDesc(ChatMessage::getCreatedAt)
        );
    }

    /**
     * 用户加入课堂
     */
    public void joinClassroom(Long classroomId, Long userId) {
        USER_CLASSROOM.put(userId, classroomId);
        CLASSROOM_SESSIONS.computeIfAbsent(classroomId, k -> new ConcurrentHashMap<>());

        // 更新在线人数
        String onlineKey = "classroom:" + classroomId + ":online";
        Long onlineCount = redisTemplate.opsForSet().add(onlineKey, userId.toString());
        redisTemplate.expire(onlineKey, java.time.Duration.ofMinutes(30));

        // 广播用户加入消息
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "user:join");
            message.put("userId", userId);
            broadcastToClassroom(classroomId, objectMapper.writeValueAsString(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户离开课堂
     */
    public void leaveClassroom(Long userId) {
        Long classroomId = USER_CLASSROOM.remove(userId);
        if (classroomId != null) {
            Map<Long, WebSocketSession> sessions = CLASSROOM_SESSIONS.get(classroomId);
            if (sessions != null) {
                sessions.remove(userId);
            }

            // 更新在线人数
            String onlineKey = "classroom:" + classroomId + ":online";
            redisTemplate.opsForSet().remove(onlineKey, userId.toString());

            // 广播用户离开消息
            try {
                Map<String, Object> message = new HashMap<>();
                message.put("type", "user:leave");
                message.put("userId", userId);
                broadcastToClassroom(classroomId, objectMapper.writeValueAsString(message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取课堂在线人数
     */
    public Long getOnlineCount(Long classroomId) {
        String onlineKey = "classroom:" + classroomId + ":online";
        return redisTemplate.opsForSet().size(onlineKey);
    }

    /**
     * 添加 WebSocket 会话
     */
    public void addSession(Long classroomId, Long userId, WebSocketSession session) {
        CLASSROOM_SESSIONS.computeIfAbsent(classroomId, k -> new ConcurrentHashMap<>())
                .put(userId, session);
        USER_CLASSROOM.put(userId, classroomId);
    }

    /**
     * 移除 WebSocket 会话
     */
    public void removeSession(Long userId) {
        leaveClassroom(userId);
    }

    /**
     * 广播消息到指定课堂
     */
    private void broadcastToClassroom(Long classroomId, String message) {
        Map<Long, WebSocketSession> sessions = CLASSROOM_SESSIONS.get(classroomId);
        if (sessions != null) {
            sessions.values().forEach(session -> {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(message));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
