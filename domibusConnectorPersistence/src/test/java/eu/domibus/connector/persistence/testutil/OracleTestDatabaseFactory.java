package eu.domibus.connector.persistence.testutil;

import org.junit.jupiter.api.Assumptions;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;
import java.util.Properties;

public class OracleTestDatabaseFactory extends AbstractTestDatabaseFactory implements TestDatabaseFactory {

    @Override
    public String getDatabaseType() {
        return "oracle";
    }

    @Override
    public String getName() {
        return "Native Oracle";
    }


    @Override
    protected String getDriverClassName() {
        return "oracle.jdbc.driver.OracleDriver";
    }
}
