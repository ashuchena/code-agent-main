-- 学生服务功能扩展 SQL
-- 请假申请表
CREATE TABLE IF NOT EXISTS `leave_request` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '请假ID',
  `user_id` BIGINT NOT NULL COMMENT '申请人ID',
  `user_name` VARCHAR(50) COMMENT '申请人姓名',
  `leave_type` VARCHAR(20) NOT NULL COMMENT '请假类型：事假/病假/公假',
  `start_date` DATE NOT NULL COMMENT '开始日期',
  `end_date` DATE NOT NULL COMMENT '结束日期',
  `reason` TEXT COMMENT '请假原因',
  `contact_phone` VARCHAR(20) COMMENT '联系电话',
  `status` INT DEFAULT 0 COMMENT '状态：0-待审批，1-已批准，2-已拒绝',
  `approve_remark` VARCHAR(255) COMMENT '审批备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `approve_time` DATETIME COMMENT '审批时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 失物招领表
CREATE TABLE IF NOT EXISTS `lost_found` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT '发布人ID',
  `user_name` VARCHAR(50) COMMENT '发布人姓名',
  `type` VARCHAR(10) NOT NULL COMMENT '类型：lost-寻物，found-招领',
  `title` VARCHAR(100) NOT NULL COMMENT '标题',
  `description` TEXT COMMENT '物品描述',
  `location` VARCHAR(200) COMMENT '丢失/捡到地点',
  `contact_phone` VARCHAR(20) COMMENT '联系方式',
  `image_url` VARCHAR(500) COMMENT '图片URL',
  `status` INT DEFAULT 0 COMMENT '状态：0-进行中，1-已找到/已归还',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 快递代取表
CREATE TABLE IF NOT EXISTS `express` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  `publisher_id` BIGINT NOT NULL COMMENT '发布人ID',
  `publisher_name` VARCHAR(50) COMMENT '发布人姓名',
  `publisher_phone` VARCHAR(20) COMMENT '发布人电话',
  `express_company` VARCHAR(50) COMMENT '快递公司',
  `tracking_no` VARCHAR(100) COMMENT '快递单号',
  `pickup_code` VARCHAR(50) NOT NULL COMMENT '取件码',
  `location` VARCHAR(200) NOT NULL COMMENT '快递所在地点',
  `reward` DECIMAL(10,2) DEFAULT 0 COMMENT '报酬',
  `remark` VARCHAR(255) COMMENT '备注',
  `status` INT DEFAULT 0 COMMENT '状态：0-待接单，1-进行中，2-已完成',
  `helper_id` BIGINT COMMENT '帮助者ID',
  `helper_name` VARCHAR(50) COMMENT '帮助者姓名',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `complete_time` DATETIME COMMENT '完成时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 志愿者活动表
CREATE TABLE IF NOT EXISTS `volunteer` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  `title` VARCHAR(200) NOT NULL COMMENT '活动标题',
  `description` TEXT COMMENT '活动描述',
  `location` VARCHAR(200) NOT NULL COMMENT '活动地点',
  `start_date` DATE NOT NULL COMMENT '活动日期',
  `start_time` VARCHAR(10) COMMENT '开始时间',
  `end_time` VARCHAR(10) COMMENT '结束时间',
  `max_participants` INT DEFAULT 20 COMMENT '名额上限',
  `current_participants` INT DEFAULT 0 COMMENT '当前报名人数',
  `hours` DECIMAL(5,1) DEFAULT 0 COMMENT '志愿时长（小时）',
  `status` INT DEFAULT 0 COMMENT '状态：0-报名中，1-进行中，2-已结束',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 志愿者报名表
CREATE TABLE IF NOT EXISTS `volunteer_signup` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  `activity_id` BIGINT NOT NULL COMMENT '活动ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `user_name` VARCHAR(50) COMMENT '用户姓名',
  `sign_up_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 勤工俭学表
CREATE TABLE IF NOT EXISTS `parttime` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  `publisher_id` BIGINT NOT NULL COMMENT '发布人ID',
  `publisher_name` VARCHAR(50) COMMENT '发布人姓名',
  `title` VARCHAR(200) NOT NULL COMMENT '职位标题',
  `description` TEXT COMMENT '工作描述',
  `work_type` VARCHAR(10) COMMENT '工作类型：校内/校外',
  `location` VARCHAR(200) COMMENT '工作地点',
  `work_time` VARCHAR(100) COMMENT '工作时间',
  `salary` DECIMAL(10,2) COMMENT '薪资/小时',
  `requirement` VARCHAR(500) COMMENT '要求',
  `contact_phone` VARCHAR(20) COMMENT '联系电话',
  `status` INT DEFAULT 0 COMMENT '状态：0-招聘中，1-已结束',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

