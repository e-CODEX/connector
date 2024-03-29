package eu.domibus.connector.persistence.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import java.time.Duration;


/**
 * Just test if persistence model loads in spring context
 * <p>
 * does not find any mistakes at db create, only if
 * hibernate can process the entities
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at>}
 */
class PersistenceModelTest {
    @Test
    void contextLoads() {
        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
            SpringApplicationBuilder springAppBuilder = new SpringApplicationBuilder(TestConfiguration.class)
                    .properties("spring.jpa.hibernate.ddl-auto=create-drop");
            ConfigurableApplicationContext appContext = springAppBuilder.run();
            System.out.println("APPCONTEXT IS STARTED...:" + appContext.isRunning());

            // test if i get a EntityManager and can execute a query...
            EntityManager bean = appContext.getBean(EntityManager.class);
            PDomibusConnectorService service = bean.find(PDomibusConnectorService.class, 1L);
        });
    }

    @Configuration
    @EnableAutoConfiguration(exclude = {LiquibaseAutoConfiguration.class})
    @EntityScan(basePackageClasses = {PDomibusConnectorPersistenceModel.class})
    static class TestConfiguration {

    }
}
