package eu.domibus.connector.persistence.service;

import java.util.Map;
import java.util.Properties;


public interface DomibusConnectorPropertiesPersistenceService {
    Properties loadProperties();

    void saveProperties(Properties properties);

    int saveProperties(Map<String, String> propertyChanges);

    void resetProperties(Properties properties);
}
