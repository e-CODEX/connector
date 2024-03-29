package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.testutil.SetupPersistenceContext;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.ConfigurableApplicationContext;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

import static eu.domibus.connector.persistence.spring.PersistenceProfiles.STORAGE_DB_PROFILE_NAME;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public abstract class CommonPersistenceDBUnitITCase {
    protected static ConfigurableApplicationContext APPLICATION_CONTEXT;
    protected DataSource ds;
    protected ConfigurableApplicationContext applicationContext;
    private DatabaseDataSourceConnection dbUnitConnection;

    @BeforeAll
    public static void beforeClass() {
        Properties defaultProperties = SetupPersistenceContext.getDefaultProperties();
        Set<String> defaultProfiles = SetupPersistenceContext.getDefaultProfiles();
        defaultProfiles.add(STORAGE_DB_PROFILE_NAME);
        APPLICATION_CONTEXT = SetupPersistenceContext.startApplicationContext(defaultProperties, defaultProfiles);
    }

    @AfterAll
    public static void afterClass() {
        APPLICATION_CONTEXT.close();
    }

    @BeforeEach
    public void setUp() throws Exception {
        this.applicationContext = APPLICATION_CONTEXT;
        // lookup type
        this.ds = APPLICATION_CONTEXT.getBean(DataSource.class);
        // lookup name
        //        this.persistenceService = APPLICATION_CONTEXT.getBean("persistenceService",
        //        DomibusConnectorPersistenceService.class);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeConnection();
    }

    public void closeConnection() {
        if (this.dbUnitConnection != null) {
            try {
                this.dbUnitConnection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                this.dbUnitConnection = null;
            }
        }
    }

    public DatabaseDataSourceConnection getDbUnitConnection() {
        if (this.dbUnitConnection != null) {
            return dbUnitConnection;
        }
        try {
            DatabaseDataSourceConnection conn = null;
            conn = new DatabaseDataSourceConnection(ds);
            DatabaseConfig config = conn.getConfig();
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new org.dbunit.ext.h2.H2DataTypeFactory());
            this.dbUnitConnection = conn;
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
