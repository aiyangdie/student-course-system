package com.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.common.Result;
import com.course.entity.Announcement;
import com.course.mapper.AnnouncementMapper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    private final AnnouncementMapper announcementMapper;

    public AnnouncementController(AnnouncementMapper announcementMapper) {
        this.announcementMapper = announcementMapper;
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") long page,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Announcement> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like(Announcement::getTitle, keyword)
                    .or().like(Announcement::getContent, keyword)
                    .or().like(Announcement::getPublisher, keyword));
        }
        qw.orderByDesc(Announcement::getId);
        return Result.ok(announcementMapper.selectPage(new Page<>(page, size), qw));
    }

    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        return Result.ok(announcementMapper.selectById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody Announcement announcement) {
        if (announcement.getStatus() == null) announcement.setStatus(1);
        if (!StringUtils.hasText(announcement.getPublisher())) announcement.setPublisher("管理员");
        announcementMapper.insert(announcement);
        return Result.ok(announcement);
    }

    @PutMapping
    public Result<?> update(@RequestBody Announcement announcement) {
        if (announcement.getId() == null) return Result.fail("ID不能为空");
        announcementMapper.updateById(announcement);
        return Result.ok(announcement);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        announcementMapper.deleteById(id);
        return Result.ok();
    }
}
