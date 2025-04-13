package com.pradesh.inventoryservice.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    @Autowired
    private Environment env;

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .baselineOnMigrate(true)
                .cleanOnValidationError(true)
                .locations("classpath:db/migration")
                .load();
        
        // Clean the database before migration (only in development)
        if (shouldCleanDatabase()) {
            flyway.clean();
        }
        
        return flyway;
    }
    
    private boolean shouldCleanDatabase() {
        // Only clean in development environments
        String[] activeProfiles = env.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("dev".equals(profile) || "test".equals(profile)) {
                return true;
            }
        }
        return false;
    }
}
