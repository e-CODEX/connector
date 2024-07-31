/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service;

import java.util.Map;
import java.util.Properties;

/**
 * Represents a service for loading, saving, and resetting connector properties.
 */
public interface DomibusConnectorPropertiesPersistenceService {
    Properties loadProperties();

    void saveProperties(Properties properties);

    int saveProperties(Map<String, String> propertyChanges);

    void resetProperties(Properties properties);
}
