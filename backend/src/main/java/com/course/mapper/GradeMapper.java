package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.entity.Grade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface GradeMapper extends BaseMapper<Grade> {

    @Select("<script>" +
            "SELECT g.*, s.name AS studentName, s.student_no AS studentNo, c.course_name AS courseName " +
            "FROM grade g " +
            "LEFT JOIN student s ON g.student_id = s.id " +
            "LEFT JOIN course c ON g.course_id = c.id " +
            "WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (s.name LIKE CONCAT('%',#{keyword},'%') OR s.student_no LIKE CONCAT('%',#{keyword},'%') OR c.course_name LIKE CONCAT('%',#{keyword},'%')) " +
            "</if>" +
            "ORDER BY g.id DESC" +
            "</script>")
    IPage<Grade> selectPageWithJoin(Page<Grade> page, @Param("keyword") String keyword);
}
