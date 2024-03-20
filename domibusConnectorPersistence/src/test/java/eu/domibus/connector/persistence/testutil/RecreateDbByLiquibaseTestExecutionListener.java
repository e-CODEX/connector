package eu.domibus.connector.persistence.testutil;

import liquibase.configuration.LiquibaseConfiguration;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import javax.sql.DataSource;

public class RecreateDbByLiquibaseTestExecutionListener extends AbstractTestExecutionListener {

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
        springLiquibase.afterPropertiesSet(); //The database get recreated here
    }

}
