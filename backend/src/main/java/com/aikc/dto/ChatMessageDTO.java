package com.aikc.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 聊天消息 DTO
 */
@Data
public class ChatMessageDTO {

    /**
     * 课堂ID
     */
    private Long classroomId;

    /**
     * 消息类型: TEXT, IMAGE, FILE
     */
    private String messageType = "TEXT";

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 文件名
     */
    private String fileName;
}
