package eu.domibus.connector.persistence.spring;

import eu.domibus.connector.common.persistence.dao.DomibusConnectorBusinessDomainDao;
import eu.domibus.connector.persistence.dao.PackageDomibusConnectorRepositories;
import eu.domibus.connector.persistence.model.PDomibusConnectorPersistenceModel;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Configuration
@EntityScan(basePackageClasses = {PDomibusConnectorPersistenceModel.class})
@EnableJpaRepositories(
        basePackageClasses = {PackageDomibusConnectorRepositories.class, DomibusConnectorBusinessDomainDao.class}
)
@EnableTransactionManagement
@PropertySource("classpath:/eu/domibus/connector/persistence/config/default-persistence-config.properties")
public class DomibusConnectorPersistenceContext {
}
