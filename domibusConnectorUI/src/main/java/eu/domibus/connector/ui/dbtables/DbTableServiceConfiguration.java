package eu.domibus.connector.ui.dbtables;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.persistence.EntityManager;
import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(prefix = DbTableServiceConfigurationProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties(DbTableServiceConfigurationProperties.class)
public class DbTableServiceConfiguration {

    @Bean
    @DependsOnDatabaseInitialization
    DbTableService dbTableService(EntityManager entityManager, DataSource ds, DbTableServiceConfigurationProperties config) {
        return new DbTableService(entityManager,
                ds, config);
    }

}
