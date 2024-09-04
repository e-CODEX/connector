/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.testutil;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * The RecreateDbByLiquibaseTestExecutionListener class is a test execution listener that is
 * responsible for recreating the database using Liquibase before and after each test class . This
 * listener extends the AbstractTestExecutionListener class and overrides the prepareTestInstance
 * and afterTestClass methods.
 *
 * <p>The listener maintains a boolean flag (alreadyCleared) to track whether the database has
 * already been cleared during the test execution.
 *
 * <p>In the prepareTestInstance method, if the alreadyCleared flag is false, it calls the
 * cleanupDatabase method to recreate the database using Liquibase. It then sets the alreadyCleared
 * flag to true to indicate that the database has been cleared.
 *
 * <p>In the afterTestClass method, it always calls the cleanupDatabase method to recreate the
 * database using Liquibase.
 *
 * <p>The cleanupDatabase method retrieves the SpringLiquibase bean from the ApplicationContext and
 * sets the 'dropFirst' property to true. It then calls the 'afterPropertiesSet' method to recreate
 * the database using Liquibase.
 */
public class RecreateDbByLiquibaseTestExecutionListener extends AbstractTestExecutionListener {
    @Override
    public final int getOrder() {
        return 2001;
    }

    private boolean alreadyCleared = false;

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        if (!alreadyCleared) {
            cleanupDatabase(testContext);
            alreadyCleared = true;
        } else {
            alreadyCleared = true;
        }
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        cleanupDatabase(testContext);
    }

    private void cleanupDatabase(TestContext testContext) throws LiquibaseException {
        ApplicationContext app = testContext.getApplicationContext();
        SpringLiquibase springLiquibase = app.getBean(SpringLiquibase.class);
        springLiquibase.setDropFirst(true);
        springLiquibase.afterPropertiesSet(); // The database get recreated here
    }
}
