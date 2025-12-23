package com.aikc.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 知识库 DTO
 */
@Data
public class KnowledgeBaseDTO {

    private Long id;

    @NotBlank(message = "知识库名称不能为空")
    private String name;

    private String description;

    /**
     * 分类: public, private
     */
    private String category = "private";

    /**
     * 标签 (逗号分隔)
     */
    private String tags;

    /**
     * 文档数量
     */
    private Integer docCount;
}
