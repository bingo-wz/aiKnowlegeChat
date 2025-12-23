-- AI 知识教研室数据库初始化脚本

CREATE DATABASE IF NOT EXISTS ai_knowledge_chat
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE ai_knowledge_chat;

-- 用户表
CREATE TABLE `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码 (BCrypt)',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `email` VARCHAR(100) COMMENT '邮箱',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `role` VARCHAR(20) NOT NULL DEFAULT 'STUDENT' COMMENT '角色: STUDENT, TEACHER, ADMIN',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (`username`),
    INDEX idx_role (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 课堂表
CREATE TABLE `classroom` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL COMMENT '课堂名称',
    `description` TEXT COMMENT '课堂描述',
    `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
    `teacher_name` VARCHAR(50) COMMENT '教师名称 (冗余)',
    `cover_image` VARCHAR(255) COMMENT '封面图片',
    `max_members` INT NOT NULL DEFAULT 50 COMMENT '最大人数',
    `current_members` INT NOT NULL DEFAULT 0 COMMENT '当前人数',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-未开始, 1-进行中, 2-已结束',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_teacher_id (`teacher_id`),
    INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课堂表';

-- 课堂成员表
CREATE TABLE `classroom_member` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `classroom_id` BIGINT NOT NULL COMMENT '课堂ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role` VARCHAR(20) NOT NULL DEFAULT 'STUDENT' COMMENT '角色: STUDENT, TEACHER',
    `joined_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    UNIQUE KEY uk_classroom_user (`classroom_id`, `user_id`),
    INDEX idx_user_id (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课堂成员表';

-- 聊天消息表
CREATE TABLE `chat_message` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `classroom_id` BIGINT NOT NULL COMMENT '课堂ID',
    `user_id` BIGINT NOT NULL COMMENT '发送者ID',
    `username` VARCHAR(50) COMMENT '发送者名称 (冗余)',
    `avatar` VARCHAR(255) COMMENT '发送者头像 (冗余)',
    `message_type` VARCHAR(20) NOT NULL DEFAULT 'TEXT' COMMENT '消息类型: TEXT, IMAGE, FILE',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `file_url` VARCHAR(255) COMMENT '文件URL',
    `file_name` VARCHAR(255) COMMENT '文件名',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_classroom_id (`classroom_id`),
    INDEX idx_created_at (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

-- 协同文档表
CREATE TABLE `doc` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `classroom_id` BIGINT NOT NULL COMMENT '课堂ID',
    `title` VARCHAR(200) NOT NULL COMMENT '文档标题',
    `content` LONGTEXT COMMENT '文档内容 (JSON格式)',
    `version` INT NOT NULL DEFAULT 1 COMMENT '版本号',
    `created_by` BIGINT NOT NULL COMMENT '创建者ID',
    `updated_by` BIGINT COMMENT '最后更新者ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_classroom_id (`classroom_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='协同文档表';

-- 知识库表
CREATE TABLE `knowledge_base` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL COMMENT '知识库名称',
    `description` TEXT COMMENT '知识库描述',
    `owner_id` BIGINT NOT NULL COMMENT '创建者ID',
    `owner_name` VARCHAR(50) COMMENT '创建者名称 (冗余)',
    `category` VARCHAR(20) NOT NULL DEFAULT 'private' COMMENT '分类: public, private',
    `tags` VARCHAR(500) COMMENT '标签 (逗号分隔)',
    `doc_count` INT NOT NULL DEFAULT 0 COMMENT '文档数量',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_owner_id (`owner_id`),
    INDEX idx_category (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库表';

-- 知识文档表
CREATE TABLE `knowledge_doc` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `kb_id` BIGINT NOT NULL COMMENT '知识库ID',
    `title` VARCHAR(200) NOT NULL COMMENT '文档标题',
    `content` LONGTEXT COMMENT '文档内容',
    `file_url` VARCHAR(255) COMMENT '文件URL',
    `file_type` VARCHAR(50) COMMENT '文件类型',
    `file_size` BIGINT COMMENT '文件大小 (字节)',
    `vector_id` VARCHAR(100) COMMENT '向量ID (向量数据库)',
    `chunk_count` INT DEFAULT 0 COMMENT '分块数量',
    `uploaded_by` BIGINT NOT NULL COMMENT '上传者ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_kb_id (`kb_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识文档表';

-- 对话会话表
CREATE TABLE `conversation` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `kb_id` BIGINT COMMENT '知识库ID (为空表示全局对话)',
    `title` VARCHAR(200) NOT NULL COMMENT '会话标题',
    `model` VARCHAR(50) NOT NULL DEFAULT 'glm-4' COMMENT '模型名称',
    `is_pinned` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶: 0-否, 1-是',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (`user_id`),
    INDEX idx_kb_id (`kb_id`),
    INDEX idx_updated_at (`updated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话会话表';

-- 对话消息表
CREATE TABLE `conversation_message` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `conversation_id` BIGINT NOT NULL COMMENT '会话ID',
    `role` VARCHAR(20) NOT NULL COMMENT '角色: USER, ASSISTANT, SYSTEM',
    `content` LONGTEXT NOT NULL COMMENT '消息内容',
    `referenced_docs` TEXT COMMENT '引用的文档ID列表 (JSON)',
    `tokens` INT COMMENT 'Token 使用量',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_conversation_id (`conversation_id`),
    INDEX idx_created_at (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话消息表';

-- 文件上传表
CREATE TABLE `file_upload` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `file_name` VARCHAR(255) NOT NULL COMMENT '文件名',
    `file_path` VARCHAR(500) NOT NULL COMMENT '存储路径',
    `file_size` BIGINT NOT NULL COMMENT '文件大小 (字节)',
    `file_type` VARCHAR(100) COMMENT '文件类型',
    `uploader_id` BIGINT NOT NULL COMMENT '上传者ID',
    `related_type` VARCHAR(50) COMMENT '关联类型: classroom, knowledge',
    `related_id` BIGINT COMMENT '关联ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_uploader_id (`uploader_id`),
    INDEX idx_related (`related_type`, `related_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件上传表';

-- 插入默认管理员用户 (密码: admin123)
INSERT INTO `user` (`username`, `password`, `nickname`, `role`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin', 'ADMIN');
