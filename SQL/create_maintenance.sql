CREATE TABLE IF NOT EXISTS campus_agent.maintenance (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  user_name VARCHAR(50),
  user_phone VARCHAR(20),
  category VARCHAR(20) NOT NULL,
  location VARCHAR(200) NOT NULL,
  description TEXT,
  images VARCHAR(1000),
  status INT DEFAULT 0,
  handler_name VARCHAR(50),
  handler_phone VARCHAR(20),
  handle_time VARCHAR(50),
  remark VARCHAR(500),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  complete_time DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
