-- 创建数据库（指定字符集和排序规则，支持中文及特殊字符）
CREATE DATABASE IF NOT EXISTS ai_edu_platform
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

-- 使用目标数据库
USE ai_edu_platform;

-- ----------------------------
-- 用户表 t_user
-- ----------------------------
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user
(
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
    username      VARCHAR(50)  NOT NULL COMMENT '登录账号',
    password      VARCHAR(100) NOT NULL COMMENT '加密后的密码',
    identity      VARCHAR(23) DEFAULT NULL COMMENT '用户身份（如学生/初级开发者等）',
    salary        INT         DEFAULT NULL COMMENT '期望/当前薪资',
    experience    TEXT        DEFAULT NULL COMMENT '项目/工作经历',
    answer_times  INT         DEFAULT NULL COMMENT '答题数量',
    average_score INT         DEFAULT NULL COMMENT '答题平均分',
    create_time   DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username) COMMENT '账号唯一约束'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户基础信息表';

-- ----------------------------
-- 题库表 t_question
-- ----------------------------
DROP TABLE IF EXISTS t_question;
CREATE TABLE t_question
(
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '题目唯一标识',
    question_name VARCHAR(200) NOT NULL COMMENT '题目名称',
    question_desc TEXT         NOT NULL COMMENT '题目需求描述',
    options       TEXT         NOT NULL COMMENT '题目技术栈选项（JSON格式）',
    target_salary INT          NOT NULL COMMENT '目标薪资',
    direction     VARCHAR(100) NOT NULL COMMENT '技术方向（如Java后端开发）',
    analysis      TEXT     DEFAULT NULL COMMENT 'AI生成的题目解析',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='AI生成题库表';

-- ----------------------------
-- 答题记录表 t_quiz_record
-- ----------------------------
DROP TABLE IF EXISTS t_quiz_record;
CREATE TABLE t_quiz_record
(
    id           BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录唯一标识',
    user_id      BIGINT NOT NULL COMMENT '用户唯一ID',
    question_id  BIGINT NOT NULL COMMENT '题目唯一ID',
    user_options TEXT   NOT NULL COMMENT '用户选择的选项（JSON格式）',
    user_answer  TEXT   NOT NULL COMMENT '用户答案（文本）',
    score        INT    NOT NULL COMMENT '本次测试得分',
    comment      TEXT          DEFAULT NULL COMMENT '评价内容',
    suggest      TEXT          DEFAULT NULL COMMENT '公司投递建议',
    reason       TEXT          DEFAULT NULL COMMENT '评分原因解析',
    true_options TEXT          DEFAULT NULL COMMENT '正确选项（JSON格式）',
    analysis     TEXT          DEFAULT NULL COMMENT 'AI生成的题目解析',
    accuracy     DECIMAL(5, 2) DEFAULT NULL COMMENT '正确率（百分比，如80.00）',
    create_time  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    PRIMARY KEY (id),
    -- 新增索引提升关联查询效率
    INDEX idx_user_id (user_id),
    INDEX idx_question_id (question_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户答题记录表';

-- ----------------------------
-- 薪资评估记录表 t_salary_report
-- ----------------------------
DROP TABLE IF EXISTS t_salary_report;
CREATE TABLE t_salary_report
(
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '报告唯一标识',
    user_id       BIGINT       NOT NULL COMMENT '用户唯一ID',
    direction     VARCHAR(100) NOT NULL COMMENT '技术方向',
    city          VARCHAR(50) DEFAULT NULL COMMENT '目标城市',
    experience    TEXT        DEFAULT NULL COMMENT '项目/工作经历',
    salary_range  VARCHAR(50) DEFAULT NULL COMMENT 'AI预测薪资区间（如10k-15k）',
    ai_suggestion TEXT        DEFAULT NULL COMMENT 'AI给出的能力提升建议',
    create_time   DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
    PRIMARY KEY (id),
    -- 新增索引提升关联查询效率
    INDEX idx_user_id (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='薪资评估报告表';

-- ----------------------------
-- 数据字典表 t_dict_data
-- ----------------------------
DROP TABLE IF EXISTS t_dict_data;
CREATE TABLE `t_dict_data`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '字典数据主键',
    `dict_type`   VARCHAR(100) NOT NULL COMMENT '字典类型（技术方向固定为tech_direction）',
    `dict_code`   VARCHAR(50)  NOT NULL COMMENT '字典编码（唯一标识，如java_backend）',
    `dict_name`   VARCHAR(100) NOT NULL COMMENT '字典名称（如Java后端开发）',
    `sort`        INT      DEFAULT 0 COMMENT '排序号（用于前端展示排序）',
    `status`      CHAR(1)  DEFAULT '0' COMMENT '状态（0-正常 1-禁用）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_type_code` (`dict_type`, `dict_code`) COMMENT '保证同一类型下编码唯一'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='数据字典表（技术方向/学历/身份等基础配置）';

-- 插入技术方向字典数据
INSERT INTO `t_dict_data` (`dict_type`, `dict_code`, `dict_name`, `sort`)
VALUES ('tech_direction', 'java_backend', 'Java后端开发', 1),
       ('tech_direction', 'python_backend', 'Python后端开发', 2),
       ('tech_direction', 'frontend_vue', 'Vue前端开发', 3),
       ('tech_direction', 'frontend_react', 'React前端开发', 4),
       ('tech_direction', 'ai_develop', 'AI开发', 5);

-- ----------------------------
-- 用户收藏表 t_quiz_collect
-- ----------------------------
DROP TABLE IF EXISTS t_quiz_collect;
CREATE TABLE t_quiz_collect
(
    id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '收藏记录唯一标识',
    user_id     BIGINT   NOT NULL COMMENT '用户唯一ID',
    question_id BIGINT   NOT NULL COMMENT '题目唯一ID',
    is_collect  CHAR(1)  NOT NULL DEFAULT '1' COMMENT '是否收藏（1=收藏，0=取消）',
    create_time DATETIME          DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    update_time DATETIME          DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_question (user_id, question_id) COMMENT '保证同一用户对同一题目只有一条收藏记录',
    INDEX idx_user_id (user_id) COMMENT '提升查询用户收藏列表的效率',
    INDEX idx_question_id (question_id) COMMENT '提升查询题目被收藏情况的效率'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户题目收藏表';