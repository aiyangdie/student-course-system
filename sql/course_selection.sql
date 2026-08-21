-- ============================================================
-- 学生选课管理系统 数据库脚本
-- MySQL 5.7+ / 8.0+
-- 字符集: utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS course_selection
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE course_selection;

-- ----------------------------
-- 系统管理员
-- ----------------------------
DROP TABLE IF EXISTS sys_admin;
CREATE TABLE sys_admin (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '登录账号',
  password    VARCHAR(100) NOT NULL COMMENT '密码(BCrypt)',
  real_name   VARCHAR(50)  DEFAULT NULL COMMENT '姓名',
  create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='系统管理员';

INSERT INTO sys_admin (username, password, real_name) VALUES
('admin', '$2a$10$OV6tsteqo7qjfJCiHsIFdeac7p8pGsQ3mxzXmseatruWwixV9kjqi', '系统管理员');

-- ----------------------------
-- 学生信息
-- ----------------------------
DROP TABLE IF EXISTS student;
CREATE TABLE student (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_no    VARCHAR(30)  NOT NULL UNIQUE COMMENT '学号',
  name          VARCHAR(50)  NOT NULL COMMENT '姓名',
  gender        VARCHAR(10)  DEFAULT '男' COMMENT '性别',
  grade         VARCHAR(20)  DEFAULT NULL COMMENT '年级',
  major         VARCHAR(100) DEFAULT NULL COMMENT '专业',
  class_name    VARCHAR(50)  DEFAULT NULL COMMENT '班级',
  phone         VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  email         VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  status        TINYINT      DEFAULT 1 COMMENT '1正常 0禁用',
  create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='学生信息';

INSERT INTO student (student_no, name, gender, grade, major, class_name, phone, email) VALUES
('2024001', '张三', '男', '2024', '计算机科学与技术', '计科2401', '13800000001', 'zhangsan@example.com'),
('2024002', '李四', '女', '2024', '软件工程', '软工2401', '13800000002', 'lisi@example.com'),
('2024003', '王五', '男', '2023', '信息管理', '信管2301', '13800000003', 'wangwu@example.com');

-- ----------------------------
-- 教师信息
-- ----------------------------
DROP TABLE IF EXISTS teacher;
CREATE TABLE teacher (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  teacher_no    VARCHAR(30)  NOT NULL UNIQUE COMMENT '工号',
  name          VARCHAR(50)  NOT NULL COMMENT '姓名',
  gender        VARCHAR(10)  DEFAULT '男' COMMENT '性别',
  title         VARCHAR(50)  DEFAULT NULL COMMENT '职称',
  department    VARCHAR(100) DEFAULT NULL COMMENT '所属院系',
  phone         VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  email         VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  status        TINYINT      DEFAULT 1 COMMENT '1在职 0离职',
  create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='教师信息';

INSERT INTO teacher (teacher_no, name, gender, title, department, phone, email) VALUES
('T001', '赵老师', '男', '副教授', '计算机学院', '13900000001', 'zhao@example.com'),
('T002', '钱老师', '女', '讲师', '计算机学院', '13900000002', 'qian@example.com'),
('T003', '孙老师', '男', '教授', '软件学院', '13900000003', 'sun@example.com');

-- ----------------------------
-- 课程信息
-- ----------------------------
DROP TABLE IF EXISTS course;
CREATE TABLE course (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_no     VARCHAR(30)  NOT NULL UNIQUE COMMENT '课程编号',
  course_name   VARCHAR(100) NOT NULL COMMENT '课程名称',
  credit        DECIMAL(3,1) DEFAULT 2.0 COMMENT '学分',
  hours         INT          DEFAULT 32 COMMENT '学时',
  course_type   VARCHAR(30)  DEFAULT '必修' COMMENT '课程类型',
  department    VARCHAR(100) DEFAULT NULL COMMENT '开课院系',
  description   VARCHAR(500) DEFAULT NULL COMMENT '课程简介',
  status        TINYINT      DEFAULT 1 COMMENT '1启用 0停用',
  create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='课程信息';

INSERT INTO course (course_no, course_name, credit, hours, course_type, department, description) VALUES
('C001', 'Java程序设计', 3.0, 48, '必修', '计算机学院', 'Java面向对象编程基础与进阶'),
('C002', '数据库原理', 3.0, 48, '必修', '计算机学院', '关系数据库理论与SQL实践'),
('C003', 'Web前端开发', 2.0, 32, '选修', '软件学院', 'HTML/CSS/JavaScript与Vue入门');

-- ----------------------------
-- 公告信息
-- ----------------------------
DROP TABLE IF EXISTS announcement;
CREATE TABLE announcement (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  title         VARCHAR(200) NOT NULL COMMENT '标题',
  content       TEXT         NOT NULL COMMENT '内容',
  publisher     VARCHAR(50)  DEFAULT '管理员' COMMENT '发布人',
  status        TINYINT      DEFAULT 1 COMMENT '1发布 0下架',
  create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='公告信息';

INSERT INTO announcement (title, content, publisher) VALUES
('关于2026春季学期选课通知', '各位同学请于3月1日至3月7日完成网上选课，逾期不予补选。', '教务处'),
('期末考试安排公示', '期末考试周为第18-19周，具体考场见教务系统。', '教务处');

-- ----------------------------
-- 排课信息
-- ----------------------------
DROP TABLE IF EXISTS course_schedule;
CREATE TABLE course_schedule (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id     BIGINT       NOT NULL COMMENT '课程ID',
  teacher_id    BIGINT       NOT NULL COMMENT '教师ID',
  semester      VARCHAR(30)  NOT NULL COMMENT '学期，如2025-2026-1',
  weekday       TINYINT      NOT NULL COMMENT '星期几 1-7',
  section_start TINYINT      NOT NULL COMMENT '开始节次',
  section_end   TINYINT      NOT NULL COMMENT '结束节次',
  classroom     VARCHAR(50)  DEFAULT NULL COMMENT '教室',
  capacity      INT          DEFAULT 50 COMMENT '容量',
  selected_count INT         DEFAULT 0 COMMENT '已选人数',
  status        TINYINT      DEFAULT 1 COMMENT '1开课 0停开',
  create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_course (course_id),
  INDEX idx_teacher (teacher_id)
) ENGINE=InnoDB COMMENT='排课信息';

INSERT INTO course_schedule (course_id, teacher_id, semester, weekday, section_start, section_end, classroom, capacity, selected_count) VALUES
(1, 1, '2025-2026-2', 1, 1, 2, '教学楼A101', 60, 1),
(2, 2, '2025-2026-2', 3, 3, 4, '教学楼B203', 50, 1),
(3, 3, '2025-2026-2', 5, 5, 6, '实验楼C301', 40, 0);

-- ----------------------------
-- 选课限制
-- ----------------------------
DROP TABLE IF EXISTS selection_limit;
CREATE TABLE selection_limit (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id       BIGINT       DEFAULT NULL COMMENT '课程ID，空表示全局',
  major           VARCHAR(100) DEFAULT NULL COMMENT '限选专业',
  grade           VARCHAR(20)  DEFAULT NULL COMMENT '限选年级',
  max_credit      DECIMAL(4,1) DEFAULT NULL COMMENT '最大学分',
  min_credit      DECIMAL(4,1) DEFAULT NULL COMMENT '最小学分',
  start_time      DATETIME     DEFAULT NULL COMMENT '选课开始时间',
  end_time        DATETIME     DEFAULT NULL COMMENT '选课结束时间',
  remark          VARCHAR(255) DEFAULT NULL COMMENT '备注',
  status          TINYINT      DEFAULT 1 COMMENT '1生效 0失效',
  create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP,
  update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='选课限制';

INSERT INTO selection_limit (course_id, major, grade, max_credit, min_credit, start_time, end_time, remark) VALUES
(NULL, NULL, NULL, 30.0, 12.0, '2026-01-01 00:00:00', '2026-12-31 23:59:59', '全局学分与时间限制'),
(3, '软件工程', '2024', NULL, NULL, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 'Web前端限软件工程2024级');

-- ----------------------------
-- 选课信息
-- ----------------------------
DROP TABLE IF EXISTS course_selection;
CREATE TABLE course_selection (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id    BIGINT       NOT NULL COMMENT '学生ID',
  schedule_id   BIGINT       NOT NULL COMMENT '排课ID',
  select_time   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
  status        TINYINT      DEFAULT 1 COMMENT '1已选 0已退',
  remark        VARCHAR(255) DEFAULT NULL,
  create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_student_schedule (student_id, schedule_id),
  INDEX idx_schedule (schedule_id)
) ENGINE=InnoDB COMMENT='选课信息';

INSERT INTO course_selection (student_id, schedule_id, status) VALUES
(1, 1, 1),
(2, 2, 1);

-- ----------------------------
-- 学生成绩
-- ----------------------------
DROP TABLE IF EXISTS grade;
CREATE TABLE grade (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id    BIGINT       NOT NULL COMMENT '学生ID',
  course_id     BIGINT       NOT NULL COMMENT '课程ID',
  schedule_id   BIGINT       DEFAULT NULL COMMENT '排课ID',
  usual_score   DECIMAL(5,2) DEFAULT NULL COMMENT '平时成绩',
  exam_score    DECIMAL(5,2) DEFAULT NULL COMMENT '考试成绩',
  total_score   DECIMAL(5,2) DEFAULT NULL COMMENT '总评成绩',
  semester      VARCHAR(30)  DEFAULT NULL COMMENT '学期',
  remark        VARCHAR(255) DEFAULT NULL,
  create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_student (student_id),
  INDEX idx_course (course_id)
) ENGINE=InnoDB COMMENT='学生成绩';

INSERT INTO grade (student_id, course_id, schedule_id, usual_score, exam_score, total_score, semester) VALUES
(1, 1, 1, 85.00, 90.00, 88.00, '2025-2026-1'),
(2, 2, 2, 78.00, 82.00, 80.00, '2025-2026-1');
