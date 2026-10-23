-- 问题反馈表（独立模块）
CREATE TABLE IF NOT EXISTS `feedback` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '反馈ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `user_name` VARCHAR(50) COMMENT '用户姓名',
  `user_phone` VARCHAR(20) COMMENT '联系电话',
  
  -- 反馈类型（6个新模块）
  `type` VARCHAR(20) NOT NULL COMMENT '反馈类型：ask-向老师提问, suggest-投诉与建议, dining-食堂餐饮反馈, dorm-宿舍管理反馈, course-课程教学反馈, safety-校园安全隐患',
  
  -- 反馈内容
  `title` VARCHAR(200) COMMENT '反馈标题',
  `content` TEXT NOT NULL COMMENT '反馈内容',
  `images` VARCHAR(1000) COMMENT '图片URLs，逗号分隔',
  
  -- 额外字段（根据类型不同使用）
  `target_teacher` VARCHAR(50) COMMENT '目标老师（ask类型使用）',
  `course_name` VARCHAR(100) COMMENT '课程名称（course类型使用）',
  `location` VARCHAR(200) COMMENT '地点（dining/dorm/safety类型使用）',
  `urgency` INT DEFAULT 0 COMMENT '紧急程度：0-普通, 1-紧急, 2-非常紧急',
  
  -- 处理状态
  `status` INT DEFAULT 0 COMMENT '状态：0-待处理, 1-处理中, 2-已回复, 3-已完成',
  `reply` TEXT COMMENT '回复内容',
  `replier_name` VARCHAR(50) COMMENT '回复人姓名',
  `reply_time` DATETIME COMMENT '回复时间',
  
  -- 时间戳
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问题反馈表';
