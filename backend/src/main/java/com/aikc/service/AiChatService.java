package com.aikc.service;

import com.aikc.dto.ChatRequest;
import com.aikc.dto.ChatResponse;
import com.aikc.entity.Conversation;
import com.aikc.entity.ConversationMessage;
import com.aikc.mapper.ConversationMapper;
import com.aikc.mapper.ConversationMessageMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * AI 对话服务
 */
@Service
public class AiChatService {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private ConversationMessageMapper messageMapper;

    @Value("${spring.ai.openai.chat.options.model:glm-4}")
    private String defaultModel;

    /**
     * 发送消息并获取 AI 回复
     */
    @Transactional
    public ChatResponse chat(ChatRequest request, Long userId) {
        // 获取或创建会话
        Long conversationId = request.getConversationId();
        Conversation conversation;

        if (conversationId == null) {
            // 创建新会话
            conversation = new Conversation();
            conversation.setUserId(userId);
            conversation.setKbId(request.getKbId());
            conversation.setTitle(generateTitle(request.getContent()));
            conversation.setModel(request.getModel() != null ? request.getModel() : defaultModel);
            conversation.setIsPinned(false);
            conversationMapper.insert(conversation);
            conversationId = conversation.getId();
        } else {
            // 获取现有会话
            conversation = conversationMapper.selectById(conversationId);
            if (conversation == null) {
                throw new RuntimeException("对话会话不存在");
            }
            // 检查权限
            if (!conversation.getUserId().equals(userId)) {
                throw new RuntimeException("无权访问该对话");
            }
        }

        // 保存用户消息
        ConversationMessage userMessage = new ConversationMessage();
        userMessage.setConversationId(conversationId);
        userMessage.setRole("USER");
        userMessage.setContent(request.getContent());
        messageMapper.insert(userMessage);

        // 构建对话历史
        List<Message> messages = buildConversationHistory(conversationId);

        // 如果指定了知识库，添加系统提示
        if (request.getKbId() != null) {
            // TODO: 从知识库检索相关内容，添加到系统提示中
            // String context = retrieveFromKnowledgeBase(request.getKbId(), request.getContent());
            // messages.add(0, new SystemMessage(context));
        }

        // 调用 AI 模型
        Prompt prompt = new Prompt(messages);
        String aiResponse = chatModel.call(prompt).getResult().getOutput().getContent();

        // 保存 AI 回复
        ConversationMessage assistantMessage = new ConversationMessage();
        assistantMessage.setConversationId(conversationId);
        assistantMessage.setRole("ASSISTANT");
        assistantMessage.setContent(aiResponse);
        messageMapper.insert(assistantMessage);

        // 更新会话时间
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationMapper.updateById(conversation);

        return ChatResponse.builder()
                .conversationId(conversationId)
                .title(conversation.getTitle())
                .content(aiResponse)
                .tokens(null) // TODO: 计算实际 token 数量
                .build();
    }

    /**
     * 获取用户的对话列表
     */
    public List<Conversation> getUserConversations(Long userId) {
        return conversationMapper.selectList(
                new LambdaQueryWrapper<Conversation>()
                        .eq(Conversation::getUserId, userId)
                        .orderByDesc(Conversation::getUpdatedAt)
        );
    }

    /**
     * 获取对话历史消息
     */
    public List<ConversationMessage> getConversationMessages(Long conversationId, Long userId) {
        // 检查权限
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null || !conversation.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问该对话");
        }

        return messageMapper.selectList(
                new LambdaQueryWrapper<ConversationMessage>()
                        .eq(ConversationMessage::getConversationId, conversationId)
                        .orderByAsc(ConversationMessage::getCreatedAt)
        );
    }

    /**
     * 删除对话
     */
    @Transactional
    public void deleteConversation(Long conversationId, Long userId) {
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("对话不存在");
        }
        if (!conversation.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该对话");
        }

        // 删除消息
        messageMapper.delete(
                new LambdaQueryWrapper<ConversationMessage>()
                        .eq(ConversationMessage::getConversationId, conversationId)
        );

        // 删除会话
        conversationMapper.deleteById(conversationId);
    }

    /**
     * 构建对话历史
     */
    private List<Message> buildConversationHistory(Long conversationId) {
        List<ConversationMessage> history = messageMapper.selectList(
                new LambdaQueryWrapper<ConversationMessage>()
                        .eq(ConversationMessage::getConversationId, conversationId)
                        .orderByAsc(ConversationMessage::getCreatedAt)
        );

        List<Message> messages = new ArrayList<>();
        for (ConversationMessage msg : history) {
            if ("USER".equals(msg.getRole())) {
                messages.add(new UserMessage(msg.getContent()));
            } else if ("ASSISTANT".equals(msg.getRole())) {
                messages.add(new AssistantMessage(msg.getContent()));
            }
        }
        return messages;
    }

    /**
     * 生成会话标题
     */
    private String generateTitle(String firstMessage) {
        // 截取前30个字符作为标题
        if (firstMessage.length() <= 30) {
            return firstMessage;
        }
        return firstMessage.substring(0, 30) + "...";
    }
}
