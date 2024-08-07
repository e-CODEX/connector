package eu.domibus.connector.controller.test.util;

import static eu.domibus.connector.persistence.spring.PersistenceProfiles.STORAGE_DB_PROFILE_NAME;

import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

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
@SpringJUnitConfig(classes = {ITCaseTestContext.class})
@TestPropertySource("classpath:application-test.properties")
@Commit
@ActiveProfiles({"ITCaseTestContext", STORAGE_DB_PROFILE_NAME})
@Sql(scripts = "/testdata.sql") // adds testdata to database like domibus-blue party
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public @interface ITCaseTestAnnotation {
}
