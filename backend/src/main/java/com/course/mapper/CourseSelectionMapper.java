package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.entity.CourseSelection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CourseSelectionMapper extends BaseMapper<CourseSelection> {

    @Select("<script>" +
            "SELECT cs.*, st.name AS studentName, st.student_no AS studentNo, " +
            "c.course_name AS courseName, t.name AS teacherName, sch.semester AS semester " +
            "FROM course_selection cs " +
            "LEFT JOIN student st ON cs.student_id = st.id " +
            "LEFT JOIN course_schedule sch ON cs.schedule_id = sch.id " +
            "LEFT JOIN course c ON sch.course_id = c.id " +
            "LEFT JOIN teacher t ON sch.teacher_id = t.id " +
            "WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (st.name LIKE CONCAT('%',#{keyword},'%') OR st.student_no LIKE CONCAT('%',#{keyword},'%') OR c.course_name LIKE CONCAT('%',#{keyword},'%')) " +
            "</if>" +
            "ORDER BY cs.id DESC" +
            "</script>")
    IPage<CourseSelection> selectPageWithJoin(Page<CourseSelection> page, @Param("keyword") String keyword);
}
