package com.aikc.service;

import com.aikc.dto.KnowledgeBaseDTO;
import com.aikc.entity.KnowledgeBase;
import com.aikc.entity.KnowledgeDoc;
import com.aikc.entity.User;
import com.aikc.mapper.KnowledgeBaseMapper;
import com.aikc.mapper.KnowledgeDocMapper;
import com.aikc.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 知识库服务
 */
@Service
public class KnowledgeService {

    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;

    @Autowired
    private KnowledgeDocMapper knowledgeDocMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 创建知识库
     */
    @Transactional
    public Long createKnowledgeBase(KnowledgeBaseDTO dto, Long userId) {
        // 获取用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 创建知识库
        KnowledgeBase kb = new KnowledgeBase();
        kb.setName(dto.getName());
        kb.setDescription(dto.getDescription());
        kb.setOwnerId(userId);
        kb.setOwnerName(user.getNickname());
        kb.setCategory(dto.getCategory());
        kb.setTags(dto.getTags());
        kb.setDocCount(0);
        kb.setStatus(1);

        knowledgeBaseMapper.insert(kb);
        return kb.getId();
    }

    /**
     * 获取知识库列表
     */
    public List<KnowledgeBaseDTO> getKnowledgeBases(Long userId) {
        // 获取公共知识库 + 用户自己的知识库
        List<KnowledgeBase> list = knowledgeBaseMapper.selectList(
                new LambdaQueryWrapper<KnowledgeBase>()
                        .and(wrapper -> wrapper
                                .eq(KnowledgeBase::getCategory, "public")
                                .or()
                                .eq(KnowledgeBase::getOwnerId, userId)
                        )
                        .eq(KnowledgeBase::getStatus, 1)
                        .orderByDesc(KnowledgeBase::getUpdatedAt)
        );

        return list.stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * 获取知识库详情
     */
    public KnowledgeBaseDTO getKnowledgeBaseDetail(Long id, Long userId) {
        KnowledgeBase kb = knowledgeBaseMapper.selectById(id);
        if (kb == null) {
            throw new RuntimeException("知识库不存在");
        }

        // 检查权限：只有公共知识库或自己的知识库可以访问
        if (!"public".equals(kb.getCategory()) && !kb.getOwnerId().equals(userId)) {
            throw new RuntimeException("无权访问该知识库");
        }

        return toDTO(kb);
    }

    /**
     * 更新知识库
     */
    @Transactional
    public void updateKnowledgeBase(Long id, KnowledgeBaseDTO dto, Long userId) {
        KnowledgeBase kb = knowledgeBaseMapper.selectById(id);
        if (kb == null) {
            throw new RuntimeException("知识库不存在");
        }

        // 检查权限：只有创建者可以修改
        if (!kb.getOwnerId().equals(userId)) {
            throw new RuntimeException("只有知识库创建者可以修改");
        }

        kb.setName(dto.getName());
        kb.setDescription(dto.getDescription());
        kb.setCategory(dto.getCategory());
        kb.setTags(dto.getTags());

        knowledgeBaseMapper.updateById(kb);
    }

    /**
     * 删除知识库
     */
    @Transactional
    public void deleteKnowledgeBase(Long id, Long userId) {
        KnowledgeBase kb = knowledgeBaseMapper.selectById(id);
        if (kb == null) {
            throw new RuntimeException("知识库不存在");
        }

        // 检查权限：只有创建者可以删除
        if (!kb.getOwnerId().equals(userId)) {
            throw new RuntimeException("只有知识库创建者可以删除");
        }

        // 删除知识库下的所有文档
        knowledgeDocMapper.delete(
                new LambdaQueryWrapper<KnowledgeDoc>().eq(KnowledgeDoc::getKbId, id)
        );

        // 删除知识库
        knowledgeBaseMapper.deleteById(id);
    }

    /**
     * 上传文档到知识库
     */
    @Transactional
    public Long uploadDocument(Long kbId, MultipartFile file, String title, Long userId) {
        // 检查知识库是否存在
        KnowledgeBase kb = knowledgeBaseMapper.selectById(kbId);
        if (kb == null) {
            throw new RuntimeException("知识库不存在");
        }

        // 检查权限：只有创建者可以上传
        if (!kb.getOwnerId().equals(userId)) {
            throw new RuntimeException("只有知识库创建者可以上传文档");
        }

        // TODO: 处理文件上传
        // 1. 保存文件到存储服务
        // 2. 提取文本内容
        // 3. 分块处理
        // 4. 向量化存储

        KnowledgeDoc doc = new KnowledgeDoc();
        doc.setKbId(kbId);
        doc.setTitle(title != null ? title : file.getOriginalFilename());
        doc.setFileUrl("/files/" + file.getOriginalFilename());
        doc.setFileType(file.getContentType());
        doc.setFileSize(file.getSize());
        doc.setUploadedBy(userId);

        knowledgeDocMapper.insert(doc);

        // 更新知识库文档数量
        kb.setDocCount(kb.getDocCount() + 1);
        knowledgeBaseMapper.updateById(kb);

        return doc.getId();
    }

    /**
     * 获取知识库文档列表
     */
    public List<KnowledgeDoc> getKnowledgeDocs(Long kbId, Long userId) {
        // 检查知识库访问权限
        KnowledgeBase kb = knowledgeBaseMapper.selectById(kbId);
        if (kb == null) {
            throw new RuntimeException("知识库不存在");
        }
        if (!"public".equals(kb.getCategory()) && !kb.getOwnerId().equals(userId)) {
            throw new RuntimeException("无权访问该知识库");
        }

        return knowledgeDocMapper.selectList(
                new LambdaQueryWrapper<KnowledgeDoc>()
                        .eq(KnowledgeDoc::getKbId, kbId)
                        .orderByDesc(KnowledgeDoc::getCreatedAt)
        );
    }

    /**
     * 删除文档
     */
    @Transactional
    public void deleteDocument(Long docId, Long userId) {
        KnowledgeDoc doc = knowledgeDocMapper.selectById(docId);
        if (doc == null) {
            throw new RuntimeException("文档不存在");
        }

        // 检查权限
        KnowledgeBase kb = knowledgeBaseMapper.selectById(doc.getKbId());
        if (kb == null || !kb.getOwnerId().equals(userId)) {
            throw new RuntimeException("无权删除该文档");
        }

        // 删除文档
        knowledgeDocMapper.deleteById(docId);

        // 更新知识库文档数量
        if (kb.getDocCount() > 0) {
            kb.setDocCount(kb.getDocCount() - 1);
            knowledgeBaseMapper.updateById(kb);
        }
    }

    /**
     * 实体转 DTO
     */
    private KnowledgeBaseDTO toDTO(KnowledgeBase kb) {
        KnowledgeBaseDTO dto = new KnowledgeBaseDTO();
        BeanUtils.copyProperties(kb, dto);
        return dto;
    }
}
