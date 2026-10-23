package com.campus.agent.config;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class UserTableInitializer {

    private final DataSource dataSource;

    public UserTableInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(createUserTableSql());
        } catch (Exception e) {
            System.err.println("[UserTable] Failed to initialize user table: " + e.getMessage());
        }
    }

    private String createUserTableSql() {
        return "CREATE TABLE IF NOT EXISTS `user` ("
                + "`id` BIGINT NOT NULL AUTO_INCREMENT,"
                + "`username` VARCHAR(64) NOT NULL,"
                + "`password` VARCHAR(128) NOT NULL,"
                + "`pushplus_token` VARCHAR(255) DEFAULT NULL,"
                + "`role` VARCHAR(20) NOT NULL DEFAULT 'user',"
                + "`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (`id`),"
                + "UNIQUE KEY `uk_username` (`username`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
    }
}
