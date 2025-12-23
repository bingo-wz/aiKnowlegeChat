package com.aikc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 对话响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    /**
     * 对话会话ID
     */
    private Long conversationId;

    /**
     * 会话标题
     */
    private String title;

    /**
     * AI 回复内容
     */
    private String content;

    /**
     * 引用的文档
     */
    private String referencedDocs;

    /**
     * Token 使用量
     */
    private Integer tokens;
}
