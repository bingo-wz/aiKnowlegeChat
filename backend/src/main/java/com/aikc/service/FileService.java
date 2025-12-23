package com.aikc.service;

import com.aikc.entity.FileUpload;
import com.aikc.mapper.FileUploadMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件服务
 */
@Service
public class FileService {

    @Autowired
    private FileUploadMapper fileUploadMapper;

    @Value("${file.upload-path:./uploads}")
    private String uploadPath;

    @Value("${file.access-url:http://localhost:8080/api/files}")
    private String accessUrl;

    /**
     * 上传文件
     */
    public FileUpload uploadFile(MultipartFile file, Long uploaderId, String relatedType, Long relatedId) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + extension;

            // 按日期创建目录
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            Path targetPath = Paths.get(uploadPath, dateDir);
            Files.createDirectories(targetPath);

            // 保存文件
            Path filePath = targetPath.resolve(newFilename);
            file.transferTo(filePath.toFile());

            // 保存文件记录
            FileUpload fileUpload = new FileUpload();
            fileUpload.setFileName(originalFilename);
            fileUpload.setFilePath(dateDir + "/" + newFilename);
            fileUpload.setFileSize(file.getSize());
            fileUpload.setFileType(file.getContentType());
            fileUpload.setUploaderId(uploaderId);
            fileUpload.setRelatedType(relatedType);
            fileUpload.setRelatedId(relatedId);

            fileUploadMapper.insert(fileUpload);
            return fileUpload;

        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件记录
     */
    public FileUpload getFileRecord(Long id) {
        return fileUploadMapper.selectById(id);
    }

    /**
     * 获取文件访问URL
     */
    public String getFileUrl(Long id) {
        FileUpload fileUpload = getFileRecord(id);
        if (fileUpload == null) {
            throw new RuntimeException("文件不存在");
        }
        return accessUrl + "/" + fileUpload.getFilePath();
    }

    /**
     * 获取文件本地路径
     */
    public Path getFilePath(String filePath) {
        return Paths.get(uploadPath, filePath);
    }

    /**
     * 删除文件
     */
    public void deleteFile(Long id, Long userId) {
        FileUpload fileUpload = fileUploadMapper.selectById(id);
        if (fileUpload == null) {
            throw new RuntimeException("文件不存在");
        }

        // 检查权限：只有上传者可以删除
        if (!fileUpload.getUploaderId().equals(userId)) {
            throw new RuntimeException("无权删除该文件");
        }

        // 删除物理文件
        try {
            Path filePath = getFilePath(fileUpload.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // 即使物理文件删除失败，也继续删除数据库记录
        }

        // 删除数据库记录
        fileUploadMapper.deleteById(id);
    }

    /**
     * 获取关联文件列表
     */
    public java.util.List<FileUpload> getRelatedFiles(String relatedType, Long relatedId) {
        return fileUploadMapper.selectList(
                new LambdaQueryWrapper<FileUpload>()
                        .eq(FileUpload::getRelatedType, relatedType)
                        .eq(FileUpload::getRelatedId, relatedId)
                        .orderByDesc(FileUpload::getCreatedAt)
        );
    }
}
