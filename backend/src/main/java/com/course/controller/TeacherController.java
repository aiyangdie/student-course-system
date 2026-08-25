package com.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.common.Result;
import com.course.entity.Teacher;
import com.course.mapper.TeacherMapper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherMapper teacherMapper;

    public TeacherController(TeacherMapper teacherMapper) {
        this.teacherMapper = teacherMapper;
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") long page,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Teacher> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like(Teacher::getName, keyword)
                    .or().like(Teacher::getTeacherNo, keyword)
                    .or().like(Teacher::getDepartment, keyword)
                    .or().like(Teacher::getTitle, keyword));
        }
        qw.orderByDesc(Teacher::getId);
        return Result.ok(teacherMapper.selectPage(new Page<>(page, size), qw));
    }

    @GetMapping("/list")
    public Result<?> list() {
        return Result.ok(teacherMapper.selectList(new LambdaQueryWrapper<Teacher>()
                .eq(Teacher::getStatus, 1).orderByAsc(Teacher::getTeacherNo)));
    }

    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        return Result.ok(teacherMapper.selectById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody Teacher teacher) {
        Long count = teacherMapper.selectCount(new LambdaQueryWrapper<Teacher>()
                .eq(Teacher::getTeacherNo, teacher.getTeacherNo()));
        if (count > 0) return Result.fail("工号已存在");
        if (teacher.getStatus() == null) teacher.setStatus(1);
        teacherMapper.insert(teacher);
        return Result.ok(teacher);
    }

    @PutMapping
    public Result<?> update(@RequestBody Teacher teacher) {
        if (teacher.getId() == null) return Result.fail("ID不能为空");
        Long count = teacherMapper.selectCount(new LambdaQueryWrapper<Teacher>()
                .eq(Teacher::getTeacherNo, teacher.getTeacherNo())
                .ne(Teacher::getId, teacher.getId()));
        if (count > 0) return Result.fail("工号已存在");
        teacherMapper.updateById(teacher);
        return Result.ok(teacher);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        teacherMapper.deleteById(id);
        return Result.ok();
    }
}
