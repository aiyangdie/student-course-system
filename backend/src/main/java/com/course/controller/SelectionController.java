package com.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.common.Result;
import com.course.entity.CourseSchedule;
import com.course.entity.CourseSelection;
import com.course.entity.SelectionLimit;
import com.course.entity.Student;
import com.course.mapper.CourseScheduleMapper;
import com.course.mapper.CourseSelectionMapper;
import com.course.mapper.SelectionLimitMapper;
import com.course.mapper.StudentMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/selection")
public class SelectionController {

    private final CourseSelectionMapper selectionMapper;
    private final CourseScheduleMapper scheduleMapper;
    private final StudentMapper studentMapper;
    private final SelectionLimitMapper limitMapper;

    public SelectionController(CourseSelectionMapper selectionMapper,
                               CourseScheduleMapper scheduleMapper,
                               StudentMapper studentMapper,
                               SelectionLimitMapper limitMapper) {
        this.selectionMapper = selectionMapper;
        this.scheduleMapper = scheduleMapper;
        this.studentMapper = studentMapper;
        this.limitMapper = limitMapper;
    }

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") long page,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(required = false) String keyword) {
        return Result.ok(selectionMapper.selectPageWithJoin(new Page<>(page, size), keyword));
    }

    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        return Result.ok(selectionMapper.selectById(id));
    }

    @PostMapping
    @Transactional
    public Result<?> add(@RequestBody CourseSelection selection) {
        if (selection.getStudentId() == null || selection.getScheduleId() == null) {
            return Result.fail("学生和排课不能为空");
        }
        if (selection.getStatus() == null) {
            selection.setStatus(1);
        }

        CourseSelection existing = selectionMapper.selectOne(new LambdaQueryWrapper<CourseSelection>()
                .eq(CourseSelection::getStudentId, selection.getStudentId())
                .eq(CourseSelection::getScheduleId, selection.getScheduleId())
                .last("LIMIT 1"));

        // 已存在且仍为已选
        if (existing != null && existing.getStatus() != null && existing.getStatus() == 1) {
            return Result.fail("该学生已选择此课程");
        }

        CourseSchedule schedule = scheduleMapper.selectById(selection.getScheduleId());
        if (schedule == null) {
            return Result.fail("排课信息不存在");
        }
        if (schedule.getStatus() != null && schedule.getStatus() == 0) {
            return Result.fail("该课程已停开");
        }

        // 仅“已选”才校验容量与限制，并占用名额
        if (selection.getStatus() == 1) {
            String limitMsg = checkLimits(selection.getStudentId(), schedule.getCourseId());
            if (limitMsg != null) {
                return Result.fail(limitMsg);
            }
            int selected = schedule.getSelectedCount() == null ? 0 : schedule.getSelectedCount();
            int capacity = schedule.getCapacity() == null ? 50 : schedule.getCapacity();
            if (selected >= capacity) {
                return Result.fail("课程容量已满");
            }
            schedule.setSelectedCount(selected + 1);
            scheduleMapper.updateById(schedule);
        }

        if (selection.getSelectTime() == null) {
            selection.setSelectTime(new Date());
        }

        // 退课后再次选课：更新原记录，避免唯一索引冲突
        if (existing != null) {
            existing.setStatus(selection.getStatus());
            existing.setRemark(selection.getRemark());
            existing.setSelectTime(selection.getSelectTime());
            selectionMapper.updateById(existing);
            return Result.ok(existing);
        }

        selectionMapper.insert(selection);
        return Result.ok(selection);
    }

    @PutMapping
    @Transactional
    public Result<?> update(@RequestBody CourseSelection selection) {
        if (selection.getId() == null) {
            return Result.fail("ID不能为空");
        }
        CourseSelection old = selectionMapper.selectById(selection.getId());
        if (old == null) {
            return Result.fail("选课记录不存在");
        }

        Integer oldStatus = old.getStatus() == null ? 1 : old.getStatus();
        Integer newStatus = selection.getStatus() == null ? oldStatus : selection.getStatus();
        Long oldScheduleId = old.getScheduleId();
        Long newScheduleId = selection.getScheduleId() == null ? oldScheduleId : selection.getScheduleId();

        // 先释放旧名额
        if (oldStatus == 1) {
            changeSelectedCount(oldScheduleId, -1);
        }

        // 再占用新名额
        if (newStatus == 1) {
            CourseSchedule schedule = scheduleMapper.selectById(newScheduleId);
            if (schedule == null) {
                // 回滚内存状态：恢复旧名额
                if (oldStatus == 1) {
                    changeSelectedCount(oldScheduleId, 1);
                }
                return Result.fail("排课信息不存在");
            }
            if (schedule.getStatus() != null && schedule.getStatus() == 0) {
                if (oldStatus == 1) {
                    changeSelectedCount(oldScheduleId, 1);
                }
                return Result.fail("该课程已停开");
            }
            int selected = schedule.getSelectedCount() == null ? 0 : schedule.getSelectedCount();
            int capacity = schedule.getCapacity() == null ? 50 : schedule.getCapacity();
            if (selected >= capacity) {
                if (oldStatus == 1) {
                    changeSelectedCount(oldScheduleId, 1);
                }
                return Result.fail("课程容量已满");
            }
            Long studentId = selection.getStudentId() == null ? old.getStudentId() : selection.getStudentId();
            String limitMsg = checkLimits(studentId, schedule.getCourseId());
            if (limitMsg != null) {
                if (oldStatus == 1) {
                    changeSelectedCount(oldScheduleId, 1);
                }
                return Result.fail(limitMsg);
            }
            schedule.setSelectedCount(selected + 1);
            scheduleMapper.updateById(schedule);
        }

        selectionMapper.updateById(selection);
        return Result.ok(selection);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<?> delete(@PathVariable Long id) {
        CourseSelection selection = selectionMapper.selectById(id);
        if (selection != null && selection.getStatus() != null && selection.getStatus() == 1) {
            changeSelectedCount(selection.getScheduleId(), -1);
        }
        selectionMapper.deleteById(id);
        return Result.ok();
    }

    private void changeSelectedCount(Long scheduleId, int delta) {
        if (scheduleId == null || delta == 0) {
            return;
        }
        CourseSchedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            return;
        }
        int selected = schedule.getSelectedCount() == null ? 0 : schedule.getSelectedCount();
        int next = selected + delta;
        if (next < 0) {
            next = 0;
        }
        schedule.setSelectedCount(next);
        scheduleMapper.updateById(schedule);
    }

    /**
     * 校验生效中的选课限制：专业、年级、时间窗。
     * @return 失败原因；通过返回 null
     */
    private String checkLimits(Long studentId, Long courseId) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            return "学生不存在";
        }
        Date now = new Date();
        List<SelectionLimit> limits = limitMapper.selectList(new LambdaQueryWrapper<SelectionLimit>()
                .eq(SelectionLimit::getStatus, 1)
                .and(w -> w.isNull(SelectionLimit::getCourseId)
                        .or()
                        .eq(SelectionLimit::getCourseId, courseId)));
        for (SelectionLimit limit : limits) {
            if (limit.getStartTime() != null && now.before(limit.getStartTime())) {
                return "未到选课开始时间";
            }
            if (limit.getEndTime() != null && now.after(limit.getEndTime())) {
                return "已过选课结束时间";
            }
            if (StringUtils.hasText(limit.getMajor())
                    && (student.getMajor() == null || !limit.getMajor().equals(student.getMajor()))) {
                return "不满足专业选课限制：" + limit.getMajor();
            }
            if (StringUtils.hasText(limit.getGrade())
                    && (student.getGrade() == null || !limit.getGrade().equals(student.getGrade()))) {
                return "不满足年级选课限制：" + limit.getGrade();
            }
        }
        return null;
    }
}
