package eu.domibus.connector.persistence.dao;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.spring.DBRiderTestExecutionListener;
import eu.domibus.connector.persistence.testutil.RecreateDbByLiquibaseTestExecutionListener;
import eu.domibus.connector.persistence.testutil.SetupPersistenceContext;
import eu.domibus.connector.testutil.junit5.SetMdcContextExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.*;

import static eu.domibus.connector.persistence.spring.PersistenceProfiles.STORAGE_DB_PROFILE_NAME;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = SetupPersistenceContext.class)
@TestPropertySource(
        properties = {
                "connector.persistence.big-data-impl-class=eu.domibus.connector.persistence.service.impl" +
                        ".DomibusConnectorBigDataPersistenceServiceJpaImpl",
                "spring.liquibase.change-log=/db/changelog/test/testdata.xml",
                //        "spring.liquibase.enabled=true",
                //        "spring.datasource.url=jdbc:h2:mem:t2",
                "spring.jta.enabled=false"
        }
)
@TestExecutionListeners(
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS,
        listeners = {RecreateDbByLiquibaseTestExecutionListener.class, // drop and create db by liquibase after each
                // TestClass
                DBRiderTestExecutionListener.class, // activate @DBRider
        }
)
@ActiveProfiles({"test", "db_h2", "connector", STORAGE_DB_PROFILE_NAME})
@DBUnit(allowEmptyFields = true)
@Inherited
@ExtendWith({SpringExtension.class, SetMdcContextExtension.class})
public @interface CommonPersistenceTest {
}
