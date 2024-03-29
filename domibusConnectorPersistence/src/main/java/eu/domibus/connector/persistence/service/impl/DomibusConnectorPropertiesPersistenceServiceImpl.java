package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.persistence.dao.DomibusConnectorPropertiesDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorProperties;
import eu.domibus.connector.persistence.service.DomibusConnectorPropertiesPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
public class DomibusConnectorPropertiesPersistenceServiceImpl implements DomibusConnectorPropertiesPersistenceService {
    DomibusConnectorPropertiesDao propertiesDao;

    @Autowired
    public void setPropertiesDao(DomibusConnectorPropertiesDao propertiesDao) {
        this.propertiesDao = propertiesDao;
    }

    @Override
    public Properties loadProperties() {
        Iterable<PDomibusConnectorProperties> allProperties = propertiesDao.findAll();
        Properties props = new Properties();
        Iterator<PDomibusConnectorProperties> it = allProperties.iterator();
        while (it.hasNext()) {
            PDomibusConnectorProperties property = it.next();
            props.setProperty(property.getPropertyName(), property.getPropertyValue());
        }
        return props;
    }

    @Override
    @Transactional
    public void saveProperties(Properties properties) {
        Set<String> stringPropertyNames = properties.stringPropertyNames();
        for (String propertyName : stringPropertyNames) {
            Optional<PDomibusConnectorProperties> property = propertiesDao.findByPropertyName(propertyName);
            PDomibusConnectorProperties prop = null;
            if (property.isPresent()) {
                if (!property.get().getPropertyValue().equals(properties.getProperty(propertyName))) {
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
                PDomibusConnectorProperties newProperty = new PDomibusConnectorProperties();
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
        Iterator<PDomibusConnectorProperties> it = allDbProperties.iterator();
        while (it.hasNext()) {
            PDomibusConnectorProperties dbProperty = it.next();
            String propertyValue = properties.getProperty(dbProperty.getPropertyName());
            if (propertyValue != null && dbProperty != null && !dbProperty.getPropertyValue().equals(propertyValue)) {
                dbProperty.setPropertyValue(propertyValue);
                propertiesDao.save(dbProperty);
            }
        }
    }
}
