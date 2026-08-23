package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.entity.SelectionLimit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SelectionLimitMapper extends BaseMapper<SelectionLimit> {

    @Select("<script>" +
            "SELECT l.*, c.course_name AS courseName FROM selection_limit l " +
            "LEFT JOIN course c ON l.course_id = c.id " +
            "WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (l.major LIKE CONCAT('%',#{keyword},'%') OR l.grade LIKE CONCAT('%',#{keyword},'%') OR c.course_name LIKE CONCAT('%',#{keyword},'%') OR l.remark LIKE CONCAT('%',#{keyword},'%')) " +
            "</if>" +
            "ORDER BY l.id DESC" +
            "</script>")
    IPage<SelectionLimit> selectPageWithJoin(Page<SelectionLimit> page, @Param("keyword") String keyword);
}
