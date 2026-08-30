package com.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.common.Result;
import com.course.entity.*;
import com.course.mapper.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;
    private final CourseMapper courseMapper;
    private final CourseSelectionMapper selectionMapper;
    private final AnnouncementMapper announcementMapper;
    private final CourseScheduleMapper scheduleMapper;
    private final GradeMapper gradeMapper;
    private final SelectionLimitMapper limitMapper;

    public DashboardController(StudentMapper studentMapper, TeacherMapper teacherMapper,
                               CourseMapper courseMapper, CourseSelectionMapper selectionMapper,
                               AnnouncementMapper announcementMapper, CourseScheduleMapper scheduleMapper,
                               GradeMapper gradeMapper, SelectionLimitMapper limitMapper) {
        this.studentMapper = studentMapper;
        this.teacherMapper = teacherMapper;
        this.courseMapper = courseMapper;
        this.selectionMapper = selectionMapper;
        this.announcementMapper = announcementMapper;
        this.scheduleMapper = scheduleMapper;
        this.gradeMapper = gradeMapper;
        this.limitMapper = limitMapper;
    }

    @GetMapping("/stats")
    public Result<?> stats() {
        Map<String, Object> map = new HashMap<>();
        map.put("studentCount", studentMapper.selectCount(null));
        map.put("teacherCount", teacherMapper.selectCount(null));
        map.put("courseCount", courseMapper.selectCount(null));
        map.put("scheduleCount", scheduleMapper.selectCount(null));
        map.put("openScheduleCount", scheduleMapper.selectCount(new LambdaQueryWrapper<CourseSchedule>().eq(CourseSchedule::getStatus, 1)));
        map.put("selectionCount", selectionMapper.selectCount(new LambdaQueryWrapper<CourseSelection>().eq(CourseSelection::getStatus, 1)));
        map.put("announcementCount", announcementMapper.selectCount(null));
        map.put("publishedAnnouncementCount", announcementMapper.selectCount(new LambdaQueryWrapper<Announcement>().eq(Announcement::getStatus, 1)));
        map.put("gradeCount", gradeMapper.selectCount(null));
        map.put("limitCount", limitMapper.selectCount(new LambdaQueryWrapper<SelectionLimit>().eq(SelectionLimit::getStatus, 1)));
        return Result.ok(map);
    }

    @GetMapping("/overview")
    public Result<?> overview() {
        Map<String, Object> map = new HashMap<>();
        map.put("stats", stats().getData());
        map.put("recentAnnouncements", announcementMapper.selectPage(
                new Page<>(1, 5),
                new LambdaQueryWrapper<Announcement>().orderByDesc(Announcement::getId)
        ).getRecords());
        map.put("recentSelections", selectionMapper.selectPageWithJoin(new Page<>(1, 5), null).getRecords());
        map.put("recentSchedules", scheduleMapper.selectPageWithJoin(new Page<>(1, 5), null).getRecords());
        return Result.ok(map);
    }
}
