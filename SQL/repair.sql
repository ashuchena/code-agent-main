-- 报修表（AI对话自动报修模块）
CREATE TABLE IF NOT EXISTS `repair` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '报修ID',
  `user_id` BIGINT NOT NULL COMMENT '申请人ID',
  `user_name` VARCHAR(50) COMMENT '申请人姓名',
  `type` VARCHAR(20) NOT NULL COMMENT '报修类型：water-水电维修, furniture-家具维修, network-网络报修, door-门窗维修, other-其他',
  `location` VARCHAR(200) NOT NULL COMMENT '报修地点',
  `description` TEXT COMMENT '故障描述',
  `contact` VARCHAR(20) COMMENT '联系方式',
  `status` INT DEFAULT 0 COMMENT '状态：0-待处理, 1-处理中, 2-已完成',
  `remark` VARCHAR(500) COMMENT '处理备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `handle_time` DATETIME COMMENT '处理时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修表（AI对话自动报修）';
