/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.common.spring;

import eu.domibus.connector.common.service.CurrentBusinessDomain;
import eu.domibus.connector.common.service.DCBusinessDomainManager;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyNameException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.lang.NonNull;

/**
 * The BusinessScopedPropertySource class is a specific implementation of the
 * {@link EnumerablePropertySource} class that provides properties scoped to a specific business
 * domain in the Domibus application.
 *
 * <p>The BusinessScopedPropertySource class extends the {@link EnumerablePropertySource} class and
 * overrides its methods to provide the source of the property values and the logic for resolving
 * property names. It uses the application context and the current business domain to dynamically
 * retrieve the properties from the {@link DCBusinessDomainManager}.
 *
 * <p>The BusinessScopedPropertySource class is typically used in the Domibus application to
 * register a property source in the application context. It is used in the
 * {@link RegisterBusinessDomainPropertySource} class to add the property source as the first
 * property source in the environment.
 */
public class BusinessScopedPropertySource
    extends EnumerablePropertySource<DomibusConnectorBusinessDomain> {
    private static final Logger LOGGER = LogManager.getLogger(BusinessScopedPropertySource.class);
    private final ApplicationContext applicationContext;

    public BusinessScopedPropertySource(ApplicationContext applicationContext) {
        super("BusinessDomain");
        this.applicationContext = applicationContext;
    }

    @Override
    public DomibusConnectorBusinessDomain getSource() {
        if (CurrentBusinessDomain.getCurrentBusinessDomain() != null) {
            DCBusinessDomainManager businessDomainManager =
                applicationContext.getBean(DCBusinessDomainManager.class);
            Optional<DomibusConnectorBusinessDomain> businessDomain =
                businessDomainManager.getBusinessDomain(
                    CurrentBusinessDomain.getCurrentBusinessDomain());
            return businessDomain.orElseThrow(() -> new IllegalArgumentException(
                "No Business Domain found for id"
                    + CurrentBusinessDomain.getCurrentBusinessDomain()));
        } else {
            return DomibusConnectorBusinessDomain.getDefaultMessageLane();
        }
    }

    @Override
    public String getProperty(String name) {
        String value;
        Map<ConfigurationPropertyName, String> m = getPropertyMap();
        try {
            value = m.get(ConfigurationPropertyName.of(name));
        } catch (InvalidConfigurationPropertyNameException ne) {
            // ignore if property name is invalid
            return null;
        }

        LOGGER.trace("Resolved property [{}={}]", name, value);
        return value;
    }

    private Map<ConfigurationPropertyName, String> getPropertyMap() {
        Map<ConfigurationPropertyName, String> m = new HashMap<>();
        if (CurrentBusinessDomain.getCurrentBusinessDomain() != null) {
            DCBusinessDomainManager businessDomainManager =
                applicationContext.getBean(DCBusinessDomainManager.class);

            businessDomainManager.getBusinessDomain(
                                     CurrentBusinessDomain.getCurrentBusinessDomain())
                                 .map(DomibusConnectorBusinessDomain::getMessageLaneProperties)
                                 .orElse(new HashMap<>())
                                 .forEach((key, v) -> m.put(ConfigurationPropertyName.of(key), v));
        }
        return m;
    }

    @Override
    @NonNull
    public String[] getPropertyNames() {
        return getPropertyMap()
            .keySet()
            .stream()
            .map(ConfigurationPropertyName::toString)
            .toArray(String[]::new);
    }
}
