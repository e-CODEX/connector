package eu.domibus.connector.ui.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import eu.domibus.connector.ui.persistence.dao.WebPersistenceDaoPackage;

/**
 * Web module specific configuration
 */
@Configuration
@PropertySource({
        "classpath:/eu/domibus/connector/web/spring/web-default-configuration.properties"
})
public class WebConfiguration {

}
