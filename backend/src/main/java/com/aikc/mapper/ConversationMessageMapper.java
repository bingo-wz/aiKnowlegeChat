package com.aikc.mapper;

import com.aikc.entity.ConversationMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对话消息 Mapper
 */
@Mapper
public interface ConversationMessageMapper extends BaseMapper<ConversationMessage> {
}
