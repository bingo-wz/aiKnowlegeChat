package com.aikc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 聊天消息实体
 */
@Data
@TableName("chat_message")
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 课堂ID
     */
    private Long classroomId;

    /**
     * 发送者ID
     */
    private Long userId;

    /**
     * 发送者名称 (冗余字段)
     */
    private String username;

    /**
     * 发送者头像 (冗余字段)
     */
    private String avatar;

    /**
     * 消息类型: TEXT-文本, IMAGE-图片, FILE-文件
     */
    private String messageType;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 文件URL (如果是文件消息)
     */
    private String fileUrl;

    /**
     * 文件名
     */
    private String fileName;

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
