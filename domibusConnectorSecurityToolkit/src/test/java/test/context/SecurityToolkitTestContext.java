package test.context;

import eu.domibus.connector.common.configuration.ConnectorConfigurationProperties;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.persistence.service.DCBusinessDomainPersistenceService;
import eu.domibus.connector.persistence.service.LargeFilePersistenceService;
import eu.domibus.connector.persistence.service.testutil.LargeFilePersistenceServicePassthroughImpl;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;


@ComponentScan(
        basePackages = {"eu.domibus.connector.security", "eu.domibus.connector.common", "eu.domibus.connector.dss"}
)
@EnableAutoConfiguration(
        exclude = {
                DataSourceAutoConfiguration.class,
                DataSourceTransactionManagerAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class
        }
)
@Configuration
//@Import({BasicDssConfigurationProperties.class})
@EnableConfigurationProperties({ConnectorConfigurationProperties.class})
public class SecurityToolkitTestContext {
    @Bean
    public LargeFilePersistenceService bigDataPersistenceService() {
        LargeFilePersistenceServicePassthroughImpl service = new LargeFilePersistenceServicePassthroughImpl();
        return Mockito.spy(service);
    }

    @Bean
    public DCBusinessDomainPersistenceService dcBusinessDomainPersistenceService() {
        DCBusinessDomainPersistenceService mock = Mockito.mock(DCBusinessDomainPersistenceService.class);
        Mockito.when(mock.findById(eq(DomibusConnectorBusinessDomain.getDefaultMessageLaneId())))
               .thenReturn(Optional.of(DomibusConnectorBusinessDomain.getDefaultMessageLane()));
        return mock;
    }
}
