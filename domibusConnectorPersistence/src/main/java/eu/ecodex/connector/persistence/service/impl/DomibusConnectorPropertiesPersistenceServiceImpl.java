/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.service.impl;

import eu.ecodex.connector.persistence.dao.DomibusConnectorPropertiesDao;
import eu.ecodex.connector.persistence.model.PDomibusConnectorProperties;
import eu.ecodex.connector.persistence.service.DomibusConnectorPropertiesPersistenceService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides persistence operations for loading, saving, and resetting Domibus connector properties.
 */
@Service
public class DomibusConnectorPropertiesPersistenceServiceImpl
    implements DomibusConnectorPropertiesPersistenceService {
    DomibusConnectorPropertiesDao propertiesDao;

    @Autowired
    public void setPropertiesDao(DomibusConnectorPropertiesDao propertiesDao) {
        this.propertiesDao = propertiesDao;
    }

    @Override
    public Properties loadProperties() {
        Iterable<PDomibusConnectorProperties> allProperties = propertiesDao.findAll();
        var properties = new Properties();
        for (PDomibusConnectorProperties property : allProperties) {
            properties.setProperty(property.getPropertyName(), property.getPropertyValue());
        }
        return properties;
    }

    @Override
    @Transactional
    public void saveProperties(Properties properties) {
        Set<String> stringPropertyNames = properties.stringPropertyNames();
        for (String propertyName : stringPropertyNames) {
            Optional<PDomibusConnectorProperties> property =
                propertiesDao.findByPropertyName(propertyName);
            PDomibusConnectorProperties prop;
            if (property.isPresent()) {
                if (!property.get().getPropertyValue()
                             .equals(properties.getProperty(propertyName))) {
                    prop = property.get();
                    prop.setPropertyValue(properties.getProperty(propertyName));
                    propertiesDao.save(prop);
                }
            } else {
                prop = new PDomibusConnectorProperties();
                prop.setPropertyName(propertyName);
                prop.setPropertyValue(properties.getProperty(propertyName));
                propertiesDao.save(prop);
            }
        }
    }

    @Override
    @Transactional
    public int saveProperties(Map<String, String> propertyChanges) {
        List<PDomibusConnectorProperties> deleteList = new ArrayList<>();
        List<PDomibusConnectorProperties> saveList = new ArrayList<>();
        for (Map.Entry<String, String> propertyChangeEntry : propertyChanges.entrySet()) {
            Optional<PDomibusConnectorProperties> property =
                propertiesDao.findByPropertyName(propertyChangeEntry.getKey());
            if (propertyChangeEntry.getValue() == null && property.isPresent()) {
                deleteList.add(property.get());
            } else if (property.isPresent()) {
                property.get().setPropertyValue(propertyChangeEntry.getValue());
                saveList.add(property.get());
            } else {
                var newProperty = new PDomibusConnectorProperties();
                newProperty.setPropertyName(propertyChangeEntry.getKey());
                newProperty.setPropertyValue(propertyChangeEntry.getValue());
                saveList.add(newProperty);
            }
        }
        propertiesDao.deleteAll(deleteList);
        propertiesDao.saveAll(saveList);
        return deleteList.size() + saveList.size();
    }

    @Override
    @Transactional
    public void resetProperties(Properties properties) {
        Iterable<PDomibusConnectorProperties> allDbProperties = propertiesDao.findAll();
        for (PDomibusConnectorProperties dbProperty : allDbProperties) {
            String propertyValue = properties.getProperty(dbProperty.getPropertyName());
            if (propertyValue != null && dbProperty != null && !dbProperty.getPropertyValue()
                                                                          .equals(propertyValue)) {
                dbProperty.setPropertyValue(propertyValue);
                propertiesDao.save(dbProperty);
            }
        }
    }
}
