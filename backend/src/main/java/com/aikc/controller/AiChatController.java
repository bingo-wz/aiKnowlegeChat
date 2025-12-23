package com.aikc.controller;

import com.aikc.common.Result;
import com.aikc.dto.ChatRequest;
import com.aikc.dto.ChatResponse;
import com.aikc.entity.Conversation;
import com.aikc.entity.ConversationMessage;
import com.aikc.security.SecurityUserDetails;
import com.aikc.service.AiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * AI 对话控制器
 */
@RestController
@RequestMapping("/ai")
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    /**
     * 发送消息
     */
    @PostMapping("/chat")
    public Result<ChatResponse> chat(@Valid @RequestBody ChatRequest request,
                                     @AuthenticationPrincipal SecurityUserDetails userDetails) {
        ChatResponse response = aiChatService.chat(request, userDetails.getUserId());
        return Result.success(response);
    }

    /**
     * 获取对话列表
     */
    @GetMapping("/conversations")
    public Result<List<Conversation>> getConversations(
            @AuthenticationPrincipal SecurityUserDetails userDetails) {
        List<Conversation> conversations = aiChatService.getUserConversations(userDetails.getUserId());
        return Result.success(conversations);
    }

    /**
     * 获取对话历史
     */
    @GetMapping("/conversations/{id}/messages")
    public Result<List<ConversationMessage>> getConversationMessages(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUserDetails userDetails) {
        List<ConversationMessage> messages = aiChatService.getConversationMessages(id, userDetails.getUserId());
        return Result.success(messages);
    }

    /**
     * 删除对话
     */
    @DeleteMapping("/conversations/{id}")
    public Result<Void> deleteConversation(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUserDetails userDetails) {
        aiChatService.deleteConversation(id, userDetails.getUserId());
        return Result.success("删除成功", null);
    }
}
