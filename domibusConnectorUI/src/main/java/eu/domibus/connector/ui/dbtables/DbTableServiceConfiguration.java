/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
