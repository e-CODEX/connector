package eu.domibus.connector.persistence.spring;

import eu.domibus.connector.persistence.dao.DomibusConnectorKeystoreDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorKeystore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class DatabaseResourceLoader {
    public static final String DB_URL_PREFIX = "dbkeystore:";

    private final DomibusConnectorKeystoreDao keystoreDao;

    public DatabaseResourceLoader(
            DomibusConnectorKeystoreDao keystoreDao) {
        this.keystoreDao = keystoreDao;
    }

    public Resource getResource(String location) {
        if (location.startsWith(DB_URL_PREFIX)) {
            // DomibusConnectorKeystoreDao databaseResourceDao =
            //  this.keystoreDao.getBean(DomibusConnectorKeystoreDao.class);
            String resourceName = location.substring(DB_URL_PREFIX.length());
            Optional<PDomibusConnectorKeystore> byUuid = keystoreDao.findByUuid(resourceName);
            if (byUuid.isPresent()) {
                return new DatabaseResource(
                        byUuid.get().getKeystore(),
                        "Database Resource: [" + resourceName + "]",
                        location
                );
            }
        }
        return null;
    }

    public static class DatabaseResource extends ByteArrayResource {
        private final String resourceString;

        private DatabaseResource(byte[] byteArray, String description, String resourceString) {
            super(byteArray, description);
            this.resourceString = resourceString;
        }

        public String getResourceString() {
            return resourceString;
        }
    }
}
