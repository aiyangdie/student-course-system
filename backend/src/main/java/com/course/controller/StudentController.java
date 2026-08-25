package com.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.common.Result;
import com.course.entity.Student;
import com.course.mapper.StudentMapper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentMapper studentMapper;

    public StudentController(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") long page,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Student> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like(Student::getName, keyword)
                    .or().like(Student::getStudentNo, keyword)
                    .or().like(Student::getMajor, keyword)
                    .or().like(Student::getClassName, keyword));
        }
        qw.orderByDesc(Student::getId);
        return Result.ok(studentMapper.selectPage(new Page<>(page, size), qw));
    }

    @GetMapping("/list")
    public Result<?> list() {
        return Result.ok(studentMapper.selectList(new LambdaQueryWrapper<Student>()
                .eq(Student::getStatus, 1).orderByAsc(Student::getStudentNo)));
    }

    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        return Result.ok(studentMapper.selectById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody Student student) {
        Long count = studentMapper.selectCount(new LambdaQueryWrapper<Student>()
                .eq(Student::getStudentNo, student.getStudentNo()));
        if (count > 0) {
            return Result.fail("学号已存在");
        }
        if (student.getStatus() == null) student.setStatus(1);
        studentMapper.insert(student);
        return Result.ok(student);
    }

    @PutMapping
    public Result<?> update(@RequestBody Student student) {
        if (student.getId() == null) return Result.fail("ID不能为空");
        Long count = studentMapper.selectCount(new LambdaQueryWrapper<Student>()
                .eq(Student::getStudentNo, student.getStudentNo())
                .ne(Student::getId, student.getId()));
        if (count > 0) return Result.fail("学号已存在");
        studentMapper.updateById(student);
        return Result.ok(student);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        studentMapper.deleteById(id);
        return Result.ok();
    }
}
