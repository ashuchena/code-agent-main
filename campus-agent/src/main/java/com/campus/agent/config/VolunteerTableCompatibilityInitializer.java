package com.campus.agent.config;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Component
public class VolunteerTableCompatibilityInitializer {

    private final DataSource dataSource;

    public VolunteerTableCompatibilityInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            if (!hasColumn(statement, "volunteer", "publisher_id")) {
                statement.executeUpdate(
                        "ALTER TABLE volunteer " +
                        "ADD COLUMN publisher_id BIGINT NULL COMMENT '发布人ID' AFTER id"
                );
                statement.executeUpdate(
                        "ALTER TABLE volunteer " +
                        "ADD INDEX idx_publisher_id (publisher_id)"
                );
                System.out.println("[VolunteerCompatibility] Added volunteer.publisher_id");
            }
        } catch (Exception e) {
            System.err.println("[VolunteerCompatibility] Failed to initialize volunteer compatibility: " + e.getMessage());
        }
    }

    private boolean hasColumn(Statement statement, String tableName, String columnName) throws Exception {
        try (ResultSet rs = statement.executeQuery("SHOW COLUMNS FROM " + tableName + " LIKE '" + columnName + "'")) {
            return rs.next();
        }
    }
}
