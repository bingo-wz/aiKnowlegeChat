package com.aikc.controller;

import com.aikc.common.Result;
import com.aikc.dto.KnowledgeBaseDTO;
import com.aikc.entity.KnowledgeDoc;
import com.aikc.security.SecurityUserDetails;
import com.aikc.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 知识库控制器
 */
@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {

    @Autowired
    private KnowledgeService knowledgeService;

    /**
     * 创建知识库
     */
    @PostMapping
    public Result<Long> createKnowledgeBase(@Valid @RequestBody KnowledgeBaseDTO dto,
                                            @AuthenticationPrincipal SecurityUserDetails userDetails) {
        Long id = knowledgeService.createKnowledgeBase(dto, userDetails.getUserId());
        return Result.success("创建成功", id);
    }

    /**
     * 获取知识库列表
     */
    @GetMapping
    public Result<List<KnowledgeBaseDTO>> getKnowledgeBases(
            @AuthenticationPrincipal SecurityUserDetails userDetails) {
        List<KnowledgeBaseDTO> list = knowledgeService.getKnowledgeBases(userDetails.getUserId());
        return Result.success(list);
    }

    /**
     * 获取知识库详情
     */
    @GetMapping("/{id}")
    public Result<KnowledgeBaseDTO> getKnowledgeBaseDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUserDetails userDetails) {
        KnowledgeBaseDTO dto = knowledgeService.getKnowledgeBaseDetail(id, userDetails.getUserId());
        return Result.success(dto);
    }

    /**
     * 更新知识库
     */
    @PutMapping("/{id}")
    public Result<Void> updateKnowledgeBase(@PathVariable Long id,
                                            @Valid @RequestBody KnowledgeBaseDTO dto,
                                            @AuthenticationPrincipal SecurityUserDetails userDetails) {
        knowledgeService.updateKnowledgeBase(id, dto, userDetails.getUserId());
        return Result.success("更新成功", null);
    }

    /**
     * 删除知识库
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteKnowledgeBase(@PathVariable Long id,
                                            @AuthenticationPrincipal SecurityUserDetails userDetails) {
        knowledgeService.deleteKnowledgeBase(id, userDetails.getUserId());
        return Result.success("删除成功", null);
    }

    /**
     * 上传文档
     */
    @PostMapping("/{id}/upload")
    public Result<Long> uploadDocument(@PathVariable Long id,
                                       @RequestParam("file") MultipartFile file,
                                       @RequestParam(value = "title", required = false) String title,
                                       @AuthenticationPrincipal SecurityUserDetails userDetails) {
        Long docId = knowledgeService.uploadDocument(id, file, title, userDetails.getUserId());
        return Result.success("上传成功", docId);
    }

    /**
     * 获取知识库文档列表
     */
    @GetMapping("/{id}/docs")
    public Result<List<KnowledgeDoc>> getKnowledgeDocs(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUserDetails userDetails) {
        List<KnowledgeDoc> docs = knowledgeService.getKnowledgeDocs(id, userDetails.getUserId());
        return Result.success(docs);
    }

    /**
     * 删除文档
     */
    @DeleteMapping("/docs/{docId}")
    public Result<Void> deleteDocument(@PathVariable Long docId,
                                       @AuthenticationPrincipal SecurityUserDetails userDetails) {
        knowledgeService.deleteDocument(docId, userDetails.getUserId());
        return Result.success("删除成功", null);
    }
}
