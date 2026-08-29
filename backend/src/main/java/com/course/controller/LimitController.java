package com.course.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.common.Result;
import com.course.entity.SelectionLimit;
import com.course.mapper.SelectionLimitMapper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/limit")
public class LimitController {

    private final SelectionLimitMapper limitMapper;

    public LimitController(SelectionLimitMapper limitMapper) {
        this.limitMapper = limitMapper;
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") long page,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(required = false) String keyword) {
        return Result.ok(limitMapper.selectPageWithJoin(new Page<>(page, size), keyword));
    }

    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        return Result.ok(limitMapper.selectById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody SelectionLimit limit) {
        if (limit.getStatus() == null) limit.setStatus(1);
        limitMapper.insert(limit);
        return Result.ok(limit);
    }

    @PutMapping
    public Result<?> update(@RequestBody SelectionLimit limit) {
        if (limit.getId() == null) return Result.fail("ID不能为空");
        limitMapper.updateById(limit);
        return Result.ok(limit);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        limitMapper.deleteById(id);
        return Result.ok();
    }
}
