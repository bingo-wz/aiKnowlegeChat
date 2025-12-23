package com.aikc.service;

import com.aikc.dto.ClassroomDTO;
import com.aikc.entity.Classroom;
import com.aikc.entity.ClassroomMember;
import com.aikc.entity.User;
import com.aikc.mapper.ClassroomMapper;
import com.aikc.mapper.ClassroomMemberMapper;
import com.aikc.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 课堂服务
 */
@Service
public class ClassroomService {

    @Autowired
    private ClassroomMapper classroomMapper;

    @Autowired
    private ClassroomMemberMapper memberMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 创建课堂
     */
    @Transactional
    public Long createClassroom(ClassroomDTO dto, Long teacherId) {
        // 获取教师信息
        User teacher = userMapper.selectById(teacherId);
        if (teacher == null) {
            throw new RuntimeException("用户不存在");
        }

        // 创建课堂
        Classroom classroom = new Classroom();
        classroom.setName(dto.getName());
        classroom.setDescription(dto.getDescription());
        classroom.setTeacherId(teacherId);
        classroom.setTeacherName(teacher.getNickname());
        classroom.setCoverImage(dto.getCoverImage());
        classroom.setMaxMembers(dto.getMaxMembers());
        classroom.setCurrentMembers(1); // 教师自动加入
        classroom.setStatus(0);

        classroomMapper.insert(classroom);

        // 教师自动加入课堂
        ClassroomMember member = new ClassroomMember();
        member.setClassroomId(classroom.getId());
        member.setUserId(teacherId);
        member.setRole("TEACHER");
        memberMapper.insert(member);

        return classroom.getId();
    }

    /**
     * 获取课堂列表 (分页)
     */
    public IPage<ClassroomDTO> getClassroomList(int page, int size, Long userId) {
        Page<Classroom> pageParam = new Page<>(page, size);

        // 查询用户加入的课堂
        List<Long> classroomIds = memberMapper.selectList(
                new LambdaQueryWrapper<ClassroomMember>().eq(ClassroomMember::getUserId, userId)
        ).stream().map(ClassroomMember::getClassroomId).collect(Collectors.toList());

        IPage<Classroom> result;
        if (classroomIds.isEmpty()) {
            result = new Page<>(page, size);
        } else {
            result = classroomMapper.selectPage(pageParam,
                    new LambdaQueryWrapper<Classroom>()
                            .in(Classroom::getId, classroomIds)
                            .orderByDesc(Classroom::getCreatedAt)
            );
        }

        // 转换为 DTO
        return result.convert(this::toDTO);
    }

    /**
     * 获取课堂详情
     */
    public ClassroomDTO getClassroomDetail(Long id) {
        Classroom classroom = classroomMapper.selectById(id);
        if (classroom == null) {
            throw new RuntimeException("课堂不存在");
        }
        return toDTO(classroom);
    }

    /**
     * 更新课堂
     */
    @Transactional
    public void updateClassroom(Long id, ClassroomDTO dto, Long userId) {
        Classroom classroom = classroomMapper.selectById(id);
        if (classroom == null) {
            throw new RuntimeException("课堂不存在");
        }

        // 检查权限：只有教师可以修改
        if (!classroom.getTeacherId().equals(userId)) {
            throw new RuntimeException("只有课堂创建者可以修改");
        }

        classroom.setName(dto.getName());
        classroom.setDescription(dto.getDescription());
        classroom.setCoverImage(dto.getCoverImage());
        classroom.setMaxMembers(dto.getMaxMembers());

        classroomMapper.updateById(classroom);
    }

    /**
     * 删除课堂
     */
    @Transactional
    public void deleteClassroom(Long id, Long userId) {
        Classroom classroom = classroomMapper.selectById(id);
        if (classroom == null) {
            throw new RuntimeException("课堂不存在");
        }

        // 检查权限：只有教师可以删除
        if (!classroom.getTeacherId().equals(userId)) {
            throw new RuntimeException("只有课堂创建者可以删除");
        }

        // 删除课堂成员
        memberMapper.delete(new LambdaQueryWrapper<ClassroomMember>().eq(ClassroomMember::getClassroomId, id));

        // 删除课堂
        classroomMapper.deleteById(id);
    }

    /**
     * 加入课堂
     */
    @Transactional
    public void joinClassroom(Long classroomId, Long userId) {
        Classroom classroom = classroomMapper.selectById(classroomId);
        if (classroom == null) {
            throw new RuntimeException("课堂不存在");
        }

        // 检查是否已加入
        Long count = memberMapper.selectCount(
                new LambdaQueryWrapper<ClassroomMember>()
                        .eq(ClassroomMember::getClassroomId, classroomId)
                        .eq(ClassroomMember::getUserId, userId)
        );

        if (count > 0) {
            throw new RuntimeException("已经加入该课堂");
        }

        // 检查人数限制
        if (classroom.getCurrentMembers() >= classroom.getMaxMembers()) {
            throw new RuntimeException("课堂人数已满");
        }

        // 加入课堂
        ClassroomMember member = new ClassroomMember();
        member.setClassroomId(classroomId);
        member.setUserId(userId);
        member.setRole("STUDENT");
        memberMapper.insert(member);

        // 更新课堂人数
        classroom.setCurrentMembers(classroom.getCurrentMembers() + 1);
        classroomMapper.updateById(classroom);
    }

    /**
     * 离开课堂
     */
    @Transactional
    public void leaveClassroom(Long classroomId, Long userId) {
        Classroom classroom = classroomMapper.selectById(classroomId);
        if (classroom == null) {
            throw new RuntimeException("课堂不存在");
        }

        // 检查是否是教师
        if (classroom.getTeacherId().equals(userId)) {
            throw new RuntimeException("教师不能离开自己的课堂");
        }

        // 删除成员记录
        memberMapper.delete(
                new LambdaQueryWrapper<ClassroomMember>()
                        .eq(ClassroomMember::getClassroomId, classroomId)
                        .eq(ClassroomMember::getUserId, userId)
        );

        // 更新课堂人数
        classroom.setCurrentMembers(Math.max(1, classroom.getCurrentMembers() - 1));
        classroomMapper.updateById(classroom);
    }

    /**
     * 实体转 DTO
     */
    private ClassroomDTO toDTO(Classroom classroom) {
        ClassroomDTO dto = new ClassroomDTO();
        dto.setId(classroom.getId());
        dto.setName(classroom.getName());
        dto.setDescription(classroom.getDescription());
        dto.setCoverImage(classroom.getCoverImage());
        dto.setMaxMembers(classroom.getMaxMembers());
        dto.setCurrentMembers(classroom.getCurrentMembers());
        dto.setStatus(classroom.getStatus());
        return dto;
    }
}
