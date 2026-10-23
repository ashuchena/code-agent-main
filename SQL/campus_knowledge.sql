CREATE TABLE IF NOT EXISTS `campus_knowledge` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `category` VARCHAR(100) DEFAULT NULL,
  `question` VARCHAR(255) NOT NULL,
  `normalized_question` VARCHAR(255) NOT NULL,
  `answer` TEXT NOT NULL,
  `source_type` VARCHAR(50) DEFAULT 'EXCEL_IMPORT',
  `source_file` VARCHAR(255) DEFAULT NULL,
  `enabled` TINYINT(1) NOT NULL DEFAULT 1,
  `hit_count` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_campus_knowledge_normalized_question` (`normalized_question`),
  KEY `idx_campus_knowledge_category` (`category`),
  KEY `idx_campus_knowledge_enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `campus_knowledge_missing` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `normalized_question` VARCHAR(255) NOT NULL,
  `latest_question` VARCHAR(255) NOT NULL,
  `answer` TEXT DEFAULT NULL,
  `hit_count` INT NOT NULL DEFAULT 1,
  `promoted` TINYINT(1) NOT NULL DEFAULT 0,
  `last_hit_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_campus_knowledge_missing_normalized_question` (`normalized_question`),
  KEY `idx_campus_knowledge_missing_promoted` (`promoted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
