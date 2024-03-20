package eu.domibus.connector.ui.dbtables;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(prefix = DbTableServiceConfigurationProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties(DbTableServiceConfigurationProperties.class)
public class DbTableServiceConfiguration {

    @Bean
    DbTableService dbTableService(EntityManager entityManager, DataSource ds, DbTableServiceConfigurationProperties config) {
        return new DbTableService(entityManager,
                ds, config);
    }

}
