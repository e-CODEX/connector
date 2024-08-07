/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.spring;

import eu.domibus.connector.persistence.dao.DomibusConnectorKeystoreDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorKeystore;
import java.util.Optional;
import lombok.Getter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * The DatabaseResourceLoader class is responsible for retrieving resources from a database
 * location.
 *
 * <p>It provides a method getResource() that takes a location and returns the corresponding
 * resource
 * if found in the database, otherwise it returns null.
 *
 * <p>This class also declares a nested static class DatabaseResource that extends the
 * ByteArrayResource class.
 * DatabaseResource represents a resource stored in a database and provides additional functionality
 * to work with byte array resources. It takes a byte array, a description, and a resource string as
 * constructor parameters.
 */
@Component
public class DatabaseResourceLoader {
    public static final String DB_URL_PREFIX = "dbkeystore:";
    private final DomibusConnectorKeystoreDao keystoreDao;

    public DatabaseResourceLoader(
        DomibusConnectorKeystoreDao keystoreDao) {
        this.keystoreDao = keystoreDao;
    }

    /**
     * Retrieves a resource from the specified location.
     *
     * @param location the location of the resource
     * @return the resource if found, otherwise null
     */
    public Resource getResource(String location) {
        if (location.startsWith(DB_URL_PREFIX)) {
            // DomibusConnectorKeystoreDao databaseResourceDao =
            //  this.keystoreDao.getBean(DomibusConnectorKeystoreDao.class);
            var resourceName = location.substring(DB_URL_PREFIX.length());
            Optional<PDomibusConnectorKeystore> byUuid = keystoreDao.findByUuid(resourceName);
            if (byUuid.isPresent()) {
                return new DatabaseResource(
                    byUuid.get().getKeystore(), "Database Resource: [" + resourceName + "]",
                    location
                );
            }
        }
        return null;
    }

    /**
     * A DatabaseResource represents a resource stored in a database. It extends the
     * ByteArrayResource class, which provides the functionality to work with byte array resources.
     * DatabaseResource is used to retrieve resources from the database.
     */
    @Getter
    public static class DatabaseResource extends ByteArrayResource {
        private final String resourceString;

        private DatabaseResource(byte[] byteArray, String description, String resourceString) {
            super(byteArray, description);
            this.resourceString = resourceString;
        }
    }
}
