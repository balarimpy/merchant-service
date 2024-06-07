package io.movmint.msp.merchant.config;

import liquibase.command.CommandResults;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionCommandStep;
import liquibase.exception.LiquibaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(LiquibaseProperties.class)
public class LiquibaseMongoDBConfig {

    private final Logger log = LoggerFactory.getLogger(LiquibaseMongoDBConfig.class);
    private final MongoProperties mongoProperties;
    private final LiquibaseProperties liquibaseProperties;
    @Value("${spring.data.mongodb.password}")
    private String mongoAdminPassword;
    private static String MONGODB_LIQUIBASE_URI = "mongodb://%s:%s@%s:%s/%s?authSource=admin";

    public LiquibaseMongoDBConfig(MongoProperties mongoProperties, LiquibaseProperties liquibaseProperties) {
        this.mongoProperties = mongoProperties;
        this.liquibaseProperties = liquibaseProperties;
    }

    @Bean
    public CommandResults liquibaseUpdate() {
        if (liquibaseProperties.isEnabled()) {
            log.info("Liquibase is enabled");
            try {
                return new CommandScope("update")
                        .addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, liquibaseProperties.getChangeLog())
                        .addArgumentValue(DbUrlConnectionCommandStep.URL_ARG, buildConnectionUrl())
                        .execute();
            } catch (LiquibaseException e) {
                log.error("error running liquibase {}", e.getMessage());
                throw new RuntimeException(e);
            }
        } else {
            log.info("Liquibase is disabled");
            return null;
        }
    }

    private String buildConnectionUrl() {
        return String.format(MONGODB_LIQUIBASE_URI, mongoProperties.getUsername(), mongoAdminPassword, mongoProperties.getHost(), mongoProperties.getPort(), mongoProperties.getDatabase());
    }
}