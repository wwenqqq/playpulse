package com.example.taskmanagement.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@RequiredArgsConstructor
@ConditionalOnExpression("'${app.role:all}' == 'feedback' or '${app.role:all}' == 'all'")
public class LegacySchemaMigration implements ApplicationRunner {

    private final JdbcTemplate jdbc;
    private final DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try (var connection = dataSource.getConnection()) {
            if (!"MySQL".equalsIgnoreCase(connection.getMetaData().getDatabaseProductName())) return;
        }

        Integer locked = jdbc.queryForObject("SELECT GET_LOCK('gameshelf_schema_migration', 30)", Integer.class);
        if (!Integer.valueOf(1).equals(locked)) throw new IllegalStateException("Could not acquire the GameShelf schema migration lock");
        try {
            jdbc.execute("ALTER TABLE feedback MODIFY COLUMN category VARCHAR(20) NOT NULL");
            jdbc.execute("ALTER TABLE feedback MODIFY COLUMN status VARCHAR(20) NOT NULL");
            jdbc.update("UPDATE feedback SET category = CASE category WHEN 'BUG' THEN 'CRITIQUE' WHEN 'BALANCE' THEN 'REVIEW' WHEN 'FEATURE' THEN 'RECOMMEND' ELSE category END");
            jdbc.update("UPDATE feedback SET status = CASE status WHEN 'REPORTED' THEN 'PUBLISHED' WHEN 'TRIAGED' THEN 'FEATURED' WHEN 'IN_PROGRESS' THEN 'PUBLISHED' WHEN 'FIXED' THEN 'ARCHIVED' ELSE status END");
        } finally {
            jdbc.queryForObject("SELECT RELEASE_LOCK('gameshelf_schema_migration')", Integer.class);
        }
    }
}
