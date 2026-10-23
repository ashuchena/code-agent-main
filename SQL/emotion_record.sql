-- 情绪记录表：存储AI对话中检测到的用户负面情绪
CREATE TABLE IF NOT EXISTS emotion_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    content VARCHAR(500) NOT NULL COMMENT '用户输入内容',
    emotion VARCHAR(50) NOT NULL COMMENT '检测到的情绪类型',
    emotion_level INT NOT NULL DEFAULT 1 COMMENT '情绪严重程度（1-轻微 2-中等 3-严重）',
    emotion_tags VARCHAR(200) COMMENT '情绪标签JSON数组',
    ai_reply VARCHAR(300) COMMENT 'AI当时的回复',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '检测时间',
    INDEX idx_user_time (user_id, create_time DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='情绪记录表';
