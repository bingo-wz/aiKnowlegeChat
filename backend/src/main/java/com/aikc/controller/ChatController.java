package com.aikc.controller;

import com.aikc.common.Result;
import com.aikc.dto.ChatMessageDTO;
import com.aikc.entity.ChatMessage;
import com.aikc.security.SecurityUserDetails;
import com.aikc.service.ChatService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 聊天控制器
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * 发送消息
     */
    @PostMapping("/send")
    public Result<Void> sendMessage(@Valid @RequestBody ChatMessageDTO dto,
                                    @AuthenticationPrincipal SecurityUserDetails userDetails) {
        try {
            chatService.sendMessage(dto, userDetails.getUserId());
            return Result.success("发送成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取聊天历史
     */
    @GetMapping("/history/{classroomId}")
    public Result<IPage<ChatMessage>> getChatHistory(
            @PathVariable Long classroomId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        IPage<ChatMessage> messages = chatService.getChatHistory(classroomId, page, size);
        return Result.success(messages);
    }

    /**
     * 获取在线人数
     */
    @GetMapping("/online/{classroomId}")
    public Result<Long> getOnlineCount(@PathVariable Long classroomId) {
        Long count = chatService.getOnlineCount(classroomId);
        return Result.success(count);
    }
}
