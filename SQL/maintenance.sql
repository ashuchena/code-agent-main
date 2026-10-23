-- 报修登记表（新独立模块）
CREATE TABLE IF NOT EXISTS `maintenance` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '报修ID',
  `user_id` BIGINT NOT NULL COMMENT '报修人ID',
  `user_name` VARCHAR(50) COMMENT '报修人姓名',
  `user_phone` VARCHAR(20) COMMENT '报修人电话',
  
  -- 报修类型
  `category` VARCHAR(20) NOT NULL COMMENT '报修类别：water-水电维修, furniture-家具维修, network-网络报修, door-门窗维修, other-其他',
  
  -- 报修详情
  `location` VARCHAR(200) NOT NULL COMMENT '报修地点',
  `description` TEXT COMMENT '问题描述',
  `images` VARCHAR(1000) COMMENT '图片URLs，逗号分隔',
  
  -- 处理状态
  `status` INT DEFAULT 0 COMMENT '状态：0-待处理, 1-处理中, 2-已完成',
  `handler_name` VARCHAR(50) COMMENT '处理人姓名',
  `handler_phone` VARCHAR(20) COMMENT '处理人电话',
  `handle_time` VARCHAR(50) COMMENT '预约处理时间',
  `remark` VARCHAR(500) COMMENT '处理备注',
  
  -- 时间戳
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报修时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `complete_time` DATETIME COMMENT '完成时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
