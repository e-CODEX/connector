/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.model;

import jakarta.persistence.EntityManager;
import java.time.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * Just test if persistence model loads in spring context.
 *
 * <p>Does not find any mistakes at db create, only if hibernate can process the entities
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at>}
 */
class PersistenceModelTest {
    // Maybe check created DB with diff:  https://github.com/vecnatechnologies/dbDiff
    @Configuration
    @EnableAutoConfiguration(exclude = {LiquibaseAutoConfiguration.class})
    @EntityScan(basePackageClasses = {PDomibusConnectorPersistenceModel.class})
    static class TestConfiguration {
    }

    @Test
    void contextLoads() {
        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
            SpringApplicationBuilder springAppBuilder =
                new SpringApplicationBuilder(TestConfiguration.class)
                    .properties("spring.jpa.hibernate.ddl-auto=create-drop");
            ConfigurableApplicationContext appContext = springAppBuilder.run();
            System.out.println("APPCONTEXT IS STARTED...:" + appContext.isRunning());

            // test if I get a EntityManager and can execute a query...
            EntityManager bean = appContext.getBean(EntityManager.class);
            PDomibusConnectorService service = bean.find(PDomibusConnectorService.class, 1L);
        });
    }
}
