package com.aikc.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * AI 对话请求 DTO
 */
@Data
public class ChatRequest {

    /**
     * 对话会话ID (为空则创建新会话)
     */
    private Long conversationId;

    /**
     * 知识库ID (可选，用于 RAG 检索)
     */
    private Long kbId;

    /**
     * 用户消息
     */
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * 模型名称 (可选)
     */
    private String model;
}
