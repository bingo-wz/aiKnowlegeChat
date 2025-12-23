package com.aikc.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课堂成员实体
 */
@Data
@TableName("classroom_member")
public class ClassroomMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Long id;

    /**
     * 课堂ID
     */
    private Long classroomId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色: STUDENT, TEACHER
     */
    private String role;

    /**
     * 加入时间
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime joinedAt;
}
