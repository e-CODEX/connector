package test.context;

import eu.domibus.connector.common.configuration.ConnectorConfigurationProperties;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.persistence.service.DCBusinessDomainPersistenceService;
import eu.domibus.connector.persistence.service.LargeFilePersistenceService;
import eu.domibus.connector.persistence.service.testutil.LargeFilePersistenceServicePassthroughImpl;
import java.util.Optional;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * The SecurityToolkitTestContext class is a configuration class that provides beans for unit
 * testing of the security toolkit. It is annotated with @Configuration to indicate that it is a
 * configuration class and @ComponentScan to specify the base packages for component scanning. It
 * also uses @EnableAutoConfiguration to exclude certain auto-configuration classes and enable
 * configuration properties.
 *
 * @see LargeFilePersistenceService
 * @see LargeFilePersistenceServicePassthroughImpl
 * @see DCBusinessDomainPersistenceService
 * @see ConnectorConfigurationProperties
 */
@ComponentScan(
    basePackages = {"eu.domibus.connector.security", "eu.domibus.connector.common",
        "eu.domibus.connector.dss"}
)
@EnableAutoConfiguration(
    exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
    }
)
@Configuration
@EnableConfigurationProperties({ConnectorConfigurationProperties.class})
public class SecurityToolkitTestContext {
    /**
     * This method creates and configures a {@code LargeFilePersistenceService} bean. It creates an
     * instance of {@code LargeFilePersistenceServicePassthroughImpl}, an implementation of the
     * {@code LargeFilePersistenceService} interface. The created instance is wrapped with a Mockito
     * spy for testing purposes.
     *
     * @return a configured instance of {@code LargeFilePersistenceService}
     * @see LargeFilePersistenceService
     * @see LargeFilePersistenceServicePassthroughImpl
     */
    @Bean
    public LargeFilePersistenceService bigDataPersistenceService() {
        var service = new LargeFilePersistenceServicePassthroughImpl();
        return Mockito.spy(service);
    }

    /**
     * The dcBusinessDomainPersistenceService method is responsible for creating a mock
     * implementation of the DCBusinessDomainPersistenceService interface.
     *
     * @return a mock object of the DCBusinessDomainPersistenceService interface configured to
     *      return the default message lane when findById() is called with the default message
     *      lane ID
     */
    @Bean
    public DCBusinessDomainPersistenceService dcBusinessDomainPersistenceService() {
        var mock = Mockito.mock(DCBusinessDomainPersistenceService.class);
        Mockito.when(mock.findById(DomibusConnectorBusinessDomain.getDefaultMessageLaneId()))
               .thenReturn(Optional.of(DomibusConnectorBusinessDomain.getDefaultMessageLane()));
        return mock;
    }
}
