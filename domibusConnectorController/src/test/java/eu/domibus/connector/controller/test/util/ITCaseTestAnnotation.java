package eu.domibus.connector.controller.test.util;


import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static eu.domibus.connector.persistence.spring.PersistenceProfiles.STORAGE_DB_PROFILE_NAME;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ITCaseTestContext.class})
@TestPropertySource("classpath:application-test.properties")
@Commit
@ActiveProfiles({"ITCaseTestContext", STORAGE_DB_PROFILE_NAME})
@Sql(scripts = "/testdata.sql") // adds testdata to database like domibus-blue party
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public @interface ITCaseTestAnnotation {
}
