package com.course.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.common.Result;
import com.course.entity.Grade;
import com.course.mapper.GradeMapper;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RestController
@RequestMapping("/grade")
public class GradeController {

    private final GradeMapper gradeMapper;

    public GradeController(GradeMapper gradeMapper) {
        this.gradeMapper = gradeMapper;
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") long page,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(required = false) String keyword) {
        return Result.ok(gradeMapper.selectPageWithJoin(new Page<>(page, size), keyword));
    }

    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        return Result.ok(gradeMapper.selectById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody Grade grade) {
        calcTotal(grade);
        gradeMapper.insert(grade);
        return Result.ok(grade);
    }

    @PutMapping
    public Result<?> update(@RequestBody Grade grade) {
        if (grade.getId() == null) return Result.fail("ID不能为空");
        calcTotal(grade);
        gradeMapper.updateById(grade);
        return Result.ok(grade);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        gradeMapper.deleteById(id);
        return Result.ok();
    }

    private void calcTotal(Grade grade) {
        // 平时+考试都有值时，始终按 4:6 重算总评，避免前端带回旧总评导致不更新
        if (grade.getUsualScore() != null && grade.getExamScore() != null) {
            BigDecimal total = grade.getUsualScore().multiply(new BigDecimal("0.4"))
                    .add(grade.getExamScore().multiply(new BigDecimal("0.6")))
                    .setScale(2, RoundingMode.HALF_UP);
            grade.setTotalScore(total);
        }
    }
}
