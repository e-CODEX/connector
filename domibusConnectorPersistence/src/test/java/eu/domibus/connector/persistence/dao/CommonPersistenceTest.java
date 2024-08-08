package eu.domibus.connector.persistence.dao;

import static eu.domibus.connector.persistence.spring.PersistenceProfiles.STORAGE_DB_PROFILE_NAME;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.spring.DBRiderTestExecutionListener;
import eu.domibus.connector.persistence.testutil.RecreateDbByLiquibaseTestExecutionListener;
import eu.domibus.connector.persistence.testutil.SetupPersistenceContext;
import eu.domibus.connector.testutil.junit5.SetMdcContextExtension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * This annotation is used to define common persistence test configurations. It is used to configure
 * the Spring Boot test environment and setup the necessary properties and dependencies for testing
 * persistence functionalities.
 *
 * <p>The annotation is applied at the class level and is inherited by the test classes that use
 * it.
 *
 * <p>The properties provided in the {@code @TestPropertySource} annotation are used to configure
 * the test environment, such as the persistence implementation class, the liquibase change log, and
 * the JTA enabled flag.
 *
 * <p>The {@code @ActiveProfiles} annotation is used to specify the active profiles for the test.
 * The profiles specified in the annotation are "test", "db_h2", "connector", and the constant value
 * STORAGE_DB_PROFILE_NAME.
 *
 * <p>The {@code @DBUnit} annotation is used to specify the DBUnit configuration for the test. The
 * allowEmptyFields property is set to true to allow empty fields in the dataset during DBUnit test
 * execution.
 *
 * <p>The {@code @ExtendWith} annotation is used to specify the extensions to be used during the
 * test execution. The SpringExtension class is used to provide integration with the Spring
 * framework, and the SetMdcContextExtension class is used to set the MDC context for logging during
 * the test execution.
 *
 * <p>The {@code @SpringBootTest} annotation is used to load the Spring Boot application context.
 * The SetupPersistenceContext class is specified as the configuration class.
 *
 * <p>The {@code @TestExecutionListeners} annotation is used to specify the test execution
 * listeners to be used. The RecreateDbByLiquibaseTestExecutionListener class is used to recreate
 * the database using Liquibase before each test class, and the DBRiderTestExecutionListener class
 * is used to activate the @DBRider annotation.
 *
 * <p>Note: The example code should not be included in the documentation. Note: The @author and
 *
 * @version tags should not be included in the documentation.
 */
@SuppressWarnings("checkstyle:LineLength")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = SetupPersistenceContext.class)
@TestPropertySource(
    properties = {
        "connector.persistence.big-data-impl-class=eu.domibus.connector.persistence.service.impl.DomibusConnectorBigDataPersistenceServiceJpaImpl",
        "spring.liquibase.change-log=/db/changelog/test/testdata.xml",
        //        "spring.liquibase.enabled=true",
        //        "spring.datasource.url=jdbc:h2:mem:t2",
        "spring.jta.enabled=false"
    }
)
@TestExecutionListeners(
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS,
    listeners = {RecreateDbByLiquibaseTestExecutionListener.class,
        // drop and create db by liquibase after each TestClass
        DBRiderTestExecutionListener.class, // activate @DBRider
    }
)
@ActiveProfiles({"test", "db_h2", "connector", STORAGE_DB_PROFILE_NAME})
@DBUnit(allowEmptyFields = true)
@Inherited
@ExtendWith({SpringExtension.class, SetMdcContextExtension.class})
public @interface CommonPersistenceTest {
}
