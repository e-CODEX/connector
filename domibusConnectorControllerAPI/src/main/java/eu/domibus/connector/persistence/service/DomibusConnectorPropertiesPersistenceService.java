/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
