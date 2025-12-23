package com.aikc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 对话消息实体
 */
@Data
@TableName("conversation_message")
public class ConversationMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
     */
    private Long conversationId;

    /**
     * 角色: USER-用户, ASSISTANT-助手, SYSTEM-系统
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 引用的文档ID列表 (JSON)
     */
    private String referencedDocs;

    /**
     * Token 使用量
     */
    private Integer tokens;

    /**
     * 是否删除: 0-未删除, 1-已删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
