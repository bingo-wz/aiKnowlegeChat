package com.aikc.controller;

import com.aikc.common.Result;
import com.aikc.entity.FileUpload;
import com.aikc.security.SecurityUserDetails;
import com.aikc.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

/**
 * 文件控制器
 */
@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public Result<FileUpload> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "relatedType", required = false) String relatedType,
            @RequestParam(value = "relatedId", required = false) Long relatedId,
            @AuthenticationPrincipal SecurityUserDetails userDetails) {
        FileUpload fileUpload = fileService.uploadFile(file, userDetails.getUserId(), relatedType, relatedId);
        return Result.success("上传成功", fileUpload);
    }

    /**
     * 获取文件信息
     */
    @GetMapping("/info/{id}")
    public Result<FileUpload> getFileInfo(@PathVariable Long id) {
        FileUpload fileUpload = fileService.getFileRecord(id);
        if (fileUpload == null) {
            return Result.notFound();
        }
        return Result.success(fileUpload);
    }

    /**
     * 下载文件
     */
    @GetMapping("/**")
    public ResponseEntity<Resource> downloadFile(@PathVariable String path) {
        try {
            // 从请求路径中提取文件路径
            // Spring 会将 /** 匹配的部分作为 path 变量
            Path filePath = fileService.getFilePath(path);

            if (!filePath.toFile().exists()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(filePath);

            // 获取文件信息
            FileUpload fileUpload = fileService.getFileRecord(null);
            String filename = "download";
            if (resource.getFilename() != null) {
                filename = resource.getFilename();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteFile(@PathVariable Long id,
                                   @AuthenticationPrincipal SecurityUserDetails userDetails) {
        fileService.deleteFile(id, userDetails.getUserId());
        return Result.success("删除成功", null);
    }

    /**
     * 获取关联文件列表
     */
    @GetMapping("/related")
    public Result<List<FileUpload>> getRelatedFiles(
            @RequestParam String relatedType,
            @RequestParam Long relatedId) {
        List<FileUpload> files = fileService.getRelatedFiles(relatedType, relatedId);
        return Result.success(files);
    }
}
