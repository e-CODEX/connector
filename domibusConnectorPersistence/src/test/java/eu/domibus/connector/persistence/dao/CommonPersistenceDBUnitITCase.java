
package eu.domibus.connector.persistence.dao;

import static eu.domibus.connector.persistence.spring.PersistenceProfiles.STORAGE_DB_PROFILE_NAME;

import eu.domibus.connector.persistence.testutil.SetupPersistenceContext;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;
import javax.sql.DataSource;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * This abstract class provides common functionality for DBUnit integration tests involving
 * persistence. It sets up the application context and database connection for testing, and provides
 * methods for closing the connection and retrieving the DBUnit connection.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public abstract class CommonPersistenceDBUnitITCase {
    protected static ConfigurableApplicationContext APPLICATION_CONTEXT;

    /**
     * This method is called before any test case in the class is executed. It sets up the
     * application context and initializes the database connection for testing.
     */
    @BeforeAll
    public static void beforeClass() {
        Properties defaultProperties = SetupPersistenceContext.getDefaultProperties();
        Set<String> defaultProfiles = SetupPersistenceContext.getDefaultProfiles();
        defaultProfiles.add(STORAGE_DB_PROFILE_NAME);
        APPLICATION_CONTEXT =
            SetupPersistenceContext.startApplicationContext(defaultProperties, defaultProfiles);
    }

    @AfterAll
    public static void afterClass() {
        APPLICATION_CONTEXT.close();
    }

    protected DataSource ds;
    private DatabaseDataSourceConnection dbUnitConnection;
    protected ConfigurableApplicationContext applicationContext;

    /**
     * Sets up the initial state of the test environment before running each test case.
     *
     * <p>It initializes the application context and retrieves the DataSource bean from the
     * application context.
     *
     * @throws Exception If an error occurs during the setup process.
     */
    @BeforeEach
    public void setUp() throws Exception {
        this.applicationContext = APPLICATION_CONTEXT;
        // lookup type
        this.ds = APPLICATION_CONTEXT.getBean(DataSource.class);
    }

    @AfterEach
    public void tearDown() {
        closeConnection();
    }

    /**
     * Closes the database connection used for DBUnit testing. If the connection is not null, it
     * will be closed and set to null. If an error occurs during the closing of the connection, a
     * RuntimeException will be thrown.
     */
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

    /**
     * Retrieves the DBUnit connection for testing purposes.
     *
     * @return The DBUnit connection.
     * @throws RuntimeException If an error occurs while retrieving the connection.
     */
    public DatabaseDataSourceConnection getDbUnitConnection() {
        if (this.dbUnitConnection != null) {
            return dbUnitConnection;
        }
        try {
            DatabaseDataSourceConnection conn;
            conn = new DatabaseDataSourceConnection(ds);
            DatabaseConfig config = conn.getConfig();
            config.setProperty(
                DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
                new org.dbunit.ext.h2.H2DataTypeFactory()
            );
            this.dbUnitConnection = conn;
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
