/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.test.util;

import static eu.ecodex.connector.persistence.spring.PersistenceProfiles.STORAGE_DB_PROFILE_NAME;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * This annotation is used to configure an integration test class.
 * It is used in conjunction with the Spring Framework to provide dependencies
 * and other configuration settings needed for testing.
 *
 * <p>The annotation is applied to the test class and includes several other annotations such
 * as @ExtendWith, @ContextConfiguration, @TestPropertySource, @Commit, @ActiveProfiles, and @Sql.
 *
 * <p>Example usage:
 *
 * <p>\@ITCaseTestAnnotation
 * public class MyIntegrationTest {
 * // test methods
 * }
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ITCaseTestContext.class})
@TestPropertySource("classpath:application-test.properties")
@Commit
@ActiveProfiles({"ITCaseTestContext", STORAGE_DB_PROFILE_NAME})
@Sql(scripts = "/testdata.sql") // adds testdata to database like domibus-blue party
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public @interface ITCaseTestAnnotation {
}
