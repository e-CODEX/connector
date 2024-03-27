package eu.domibus.connector.common.spring;

import eu.domibus.connector.tools.logging.LoggingMarker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MutablePropertySources;


public class RegisterBusinessDomainPropertySource implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final Logger LOGGER = LogManager.getLogger(RegisterBusinessDomainPropertySource.class);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        LOGGER.debug(
                LoggingMarker.Log4jMarker.CONFIG,
                "Registering business scoped property source as first property source"
        );
        MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
        propertySources.addFirst(new BusinessScopedPropertySource(applicationContext));
    }
}
