/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.dbtables;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The {@code DbTableServiceConfiguration} class is a Spring configuration class that enables the
 * DbTableService for a specified set of database tables.
 */
@Configuration
@ConditionalOnProperty(
    prefix = DbTableServiceConfigurationProperties.PREFIX, value = "enabled", havingValue = "true"
)
@EnableConfigurationProperties(DbTableServiceConfigurationProperties.class)
public class DbTableServiceConfiguration {
    @Bean
    DbTableService dbTableService(
        EntityManager entityManager, DataSource ds, DbTableServiceConfigurationProperties config) {
        return new DbTableService(entityManager,
                                  ds, config
        );
    }
}
