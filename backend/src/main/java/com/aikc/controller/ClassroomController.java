package com.aikc.controller;

import com.aikc.common.Result;
import com.aikc.dto.ClassroomDTO;
import com.aikc.security.SecurityUserDetails;
import com.aikc.service.ClassroomService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 课堂控制器
 */
@RestController
@RequestMapping("/classroom")
public class ClassroomController {

    @Autowired
    private ClassroomService classroomService;

    /**
     * 创建课堂
     */
    @PostMapping
    public Result<Long> createClassroom(@Valid @RequestBody ClassroomDTO dto,
                                        @AuthenticationPrincipal SecurityUserDetails userDetails) {
        Long id = classroomService.createClassroom(dto, userDetails.getUserId());
        return Result.success("创建成功", id);
    }

    /**
     * 获取课堂列表
     */
    @GetMapping
    public Result<IPage<ClassroomDTO>> getClassroomList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal SecurityUserDetails userDetails) {
        IPage<ClassroomDTO> result = classroomService.getClassroomList(page, size, userDetails.getUserId());
        return Result.success(result);
    }

    /**
     * 获取课堂详情
     */
    @GetMapping("/{id}")
    public Result<ClassroomDTO> getClassroomDetail(@PathVariable Long id) {
        ClassroomDTO dto = classroomService.getClassroomDetail(id);
        return Result.success(dto);
    }

    /**
     * 更新课堂
     */
    @PutMapping("/{id}")
    public Result<Void> updateClassroom(@PathVariable Long id,
                                        @Valid @RequestBody ClassroomDTO dto,
                                        @AuthenticationPrincipal SecurityUserDetails userDetails) {
        classroomService.updateClassroom(id, dto, userDetails.getUserId());
        return Result.success("更新成功", null);
    }

    /**
     * 删除课堂
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteClassroom(@PathVariable Long id,
                                        @AuthenticationPrincipal SecurityUserDetails userDetails) {
        classroomService.deleteClassroom(id, userDetails.getUserId());
        return Result.success("删除成功", null);
    }

    /**
     * 加入课堂
     */
    @PostMapping("/{id}/join")
    public Result<Void> joinClassroom(@PathVariable Long id,
                                      @AuthenticationPrincipal SecurityUserDetails userDetails) {
        classroomService.joinClassroom(id, userDetails.getUserId());
        return Result.success("加入成功", null);
    }

    /**
     * 离开课堂
     */
    @PostMapping("/{id}/leave")
    public Result<Void> leaveClassroom(@PathVariable Long id,
                                       @AuthenticationPrincipal SecurityUserDetails userDetails) {
        classroomService.leaveClassroom(id, userDetails.getUserId());
        return Result.success("离开成功", null);
    }
}
