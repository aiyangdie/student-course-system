package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.entity.CourseSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CourseScheduleMapper extends BaseMapper<CourseSchedule> {

    @Select("<script>" +
            "SELECT s.*, c.course_name AS courseName, c.course_no AS courseNo, t.name AS teacherName " +
            "FROM course_schedule s " +
            "LEFT JOIN course c ON s.course_id = c.id " +
            "LEFT JOIN teacher t ON s.teacher_id = t.id " +
            "WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (c.course_name LIKE CONCAT('%',#{keyword},'%') OR t.name LIKE CONCAT('%',#{keyword},'%') OR s.classroom LIKE CONCAT('%',#{keyword},'%')) " +
            "</if>" +
            "ORDER BY s.id DESC" +
            "</script>")
    IPage<CourseSchedule> selectPageWithJoin(Page<CourseSchedule> page, @Param("keyword") String keyword);
}
