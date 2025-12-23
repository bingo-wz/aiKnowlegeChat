package com.aikc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课堂实体
 */
@Data
@TableName("classroom")
public class Classroom implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 课堂名称
     */
    private String name;

    /**
     * 课堂描述
     */
    private String description;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 教师名称 (冗余字段)
     */
    private String teacherName;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 最大人数
     */
    private Integer maxMembers;

    /**
     * 当前人数
     */
    private Integer currentMembers;

    /**
     * 状态: 0-未开始, 1-进行中, 2-已结束
     */
    private Integer status;

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

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
