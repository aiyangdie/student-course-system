package com.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.common.Result;
import com.course.entity.Course;
import com.course.mapper.CourseMapper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
public class CourseController {

    private final CourseMapper courseMapper;

    public CourseController(CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") long page,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Course> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like(Course::getCourseName, keyword)
                    .or().like(Course::getCourseNo, keyword)
                    .or().like(Course::getDepartment, keyword));
        }
        qw.orderByDesc(Course::getId);
        return Result.ok(courseMapper.selectPage(new Page<>(page, size), qw));
    }

    @GetMapping("/list")
    public Result<?> list() {
        return Result.ok(courseMapper.selectList(new LambdaQueryWrapper<Course>()
                .eq(Course::getStatus, 1).orderByAsc(Course::getCourseNo)));
    }

    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        return Result.ok(courseMapper.selectById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody Course course) {
        Long count = courseMapper.selectCount(new LambdaQueryWrapper<Course>()
                .eq(Course::getCourseNo, course.getCourseNo()));
        if (count > 0) return Result.fail("课程编号已存在");
        if (course.getStatus() == null) course.setStatus(1);
        courseMapper.insert(course);
        return Result.ok(course);
    }

    @PutMapping
    public Result<?> update(@RequestBody Course course) {
        if (course.getId() == null) return Result.fail("ID不能为空");
        Long count = courseMapper.selectCount(new LambdaQueryWrapper<Course>()
                .eq(Course::getCourseNo, course.getCourseNo())
                .ne(Course::getId, course.getId()));
        if (count > 0) return Result.fail("课程编号已存在");
        courseMapper.updateById(course);
        return Result.ok(course);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        courseMapper.deleteById(id);
        return Result.ok();
    }
}
