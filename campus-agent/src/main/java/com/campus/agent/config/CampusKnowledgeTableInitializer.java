package com.campus.agent.config;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class CampusKnowledgeTableInitializer {

    private final DataSource dataSource;

    public CampusKnowledgeTableInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(createCampusKnowledgeSql());
            statement.executeUpdate(createCampusKnowledgeMissingSql());
            statement.executeUpdate(createEmotionRecordSql());
            statement.executeUpdate(createSystemConfigSql());
            statement.executeUpdate(initDefaultSystemConfigSql());
        } catch (Exception e) {
            System.err.println("[CampusKnowledge] Failed to initialize tables: " + e.getMessage());
        }
    }

    private String createCampusKnowledgeSql() {
        return "CREATE TABLE IF NOT EXISTS campus_knowledge ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY,"
                + "category VARCHAR(100) DEFAULT NULL,"
                + "question VARCHAR(255) NOT NULL,"
                + "normalized_question VARCHAR(255) NOT NULL,"
                + "answer TEXT NOT NULL,"
                + "source_type VARCHAR(50) DEFAULT 'EXCEL_IMPORT',"
                + "source_file VARCHAR(255) DEFAULT NULL,"
                + "enabled TINYINT(1) NOT NULL DEFAULT 1,"
                + "hit_count INT NOT NULL DEFAULT 0,"
                + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                + "UNIQUE KEY uk_campus_knowledge_normalized_question (normalized_question),"
                + "KEY idx_campus_knowledge_category (category),"
                + "KEY idx_campus_knowledge_enabled (enabled)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
    }

    private String createCampusKnowledgeMissingSql() {
        return "CREATE TABLE IF NOT EXISTS campus_knowledge_missing ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY,"
                + "normalized_question VARCHAR(255) NOT NULL,"
                + "latest_question VARCHAR(255) NOT NULL,"
                + "answer TEXT DEFAULT NULL,"
                + "hit_count INT NOT NULL DEFAULT 1,"
                + "promoted TINYINT(1) NOT NULL DEFAULT 0,"
                + "last_hit_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                + "UNIQUE KEY uk_campus_knowledge_missing_normalized_question (normalized_question),"
                + "KEY idx_campus_knowledge_missing_promoted (promoted)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
    }

    private String createEmotionRecordSql() {
        return "CREATE TABLE IF NOT EXISTS emotion_record ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY,"
                + "user_id BIGINT,"
                + "content VARCHAR(500),"
                + "emotion VARCHAR(100),"
                + "emotion_level INT,"
                + "emotion_tags TEXT,"
                + "ai_reply VARCHAR(300),"
                + "create_time DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
    }

    private String createSystemConfigSql() {
        return "CREATE TABLE IF NOT EXISTS system_config ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY,"
                + "config_key VARCHAR(120) NOT NULL,"
                + "config_value VARCHAR(1000) DEFAULT NULL,"
                + "description VARCHAR(255) DEFAULT NULL,"
                + "create_time DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                + "UNIQUE KEY uk_system_config_key (config_key)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
    }

    private String initDefaultSystemConfigSql() {
        return "INSERT IGNORE INTO system_config (config_key, config_value, description) VALUES "
                + "('campus.school.name', '广州松田职业学院', '当前院校名称'),"
                + "('campus.knowledge.auto.learn.threshold', '3', '知识库高频问题自动学习阈值')";
    }
}
