package com.course.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.common.Result;
import com.course.entity.CourseSchedule;
import com.course.mapper.CourseScheduleMapper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final CourseScheduleMapper scheduleMapper;

    public ScheduleController(CourseScheduleMapper scheduleMapper) {
        this.scheduleMapper = scheduleMapper;
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") long page,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(required = false) String keyword) {
        return Result.ok(scheduleMapper.selectPageWithJoin(new Page<>(page, size), keyword));
    }

    @GetMapping("/list")
    public Result<?> list() {
        return Result.ok(scheduleMapper.selectPageWithJoin(new Page<>(1, 1000), null).getRecords());
    }

    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        return Result.ok(scheduleMapper.selectById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody CourseSchedule schedule) {
        if (schedule.getStatus() == null) schedule.setStatus(1);
        if (schedule.getSelectedCount() == null) schedule.setSelectedCount(0);
        if (schedule.getCapacity() == null) schedule.setCapacity(50);
        scheduleMapper.insert(schedule);
        return Result.ok(schedule);
    }

    @PutMapping
    public Result<?> update(@RequestBody CourseSchedule schedule) {
        if (schedule.getId() == null) return Result.fail("ID不能为空");
        scheduleMapper.updateById(schedule);
        return Result.ok(schedule);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        scheduleMapper.deleteById(id);
        return Result.ok();
    }
}
