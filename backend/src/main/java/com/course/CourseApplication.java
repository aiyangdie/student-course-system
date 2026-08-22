package com.course;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.course.mapper")
public class CourseApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourseApplication.class, args);
        System.out.println("========================================");
        System.out.println(" 学生选课管理系统启动成功");
        System.out.println(" 接口地址: http://localhost:8088/api");
        System.out.println("========================================");
    }
}
