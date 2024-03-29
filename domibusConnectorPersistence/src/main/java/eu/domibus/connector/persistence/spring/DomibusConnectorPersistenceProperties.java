package eu.domibus.connector.persistence.spring;

import eu.domibus.connector.persistence.largefiles.provider.LargeFilePersistenceProvider;
import eu.domibus.connector.persistence.largefiles.provider.LargeFilePersistenceServiceFilesystemImpl;
import eu.domibus.connector.persistence.largefiles.provider.LargeFilePersistenceServiceJpaImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


/**
 * Contains all properties related to the persistence module
 * except DataSource which is configured via spring boot datasource
 */
@Component
@ConfigurationProperties(prefix = DomibusConnectorPersistenceProperties.PREFIX)
@PropertySource("classpath:eu/domibus/connector/persistence/config/default-persistence-config.properties")
public class DomibusConnectorPersistenceProperties {
    public static final String PREFIX = "connector.persistence";

    /**
     * Which big LargeFileImpl should be used by default
     * part of the connector are
     * <ul>
     *     <li>FileBased ({@link LargeFilePersistenceServiceFilesystemImpl#getClass()}</li>
     *     <li>DatabaseBased ({@link LargeFilePersistenceServiceJpaImpl#getClass()}</li>
     * </ul>
     */
    public Class<? extends LargeFilePersistenceProvider> defaultLargeFileProviderClass =
            LargeFilePersistenceServiceFilesystemImpl.class;
    /**
     * Which big LargeFileImpl should be used by default
     * part of the connector are
     * <ul>
     *     <li>FileBased ({@link LargeFilePersistenceServiceFilesystemImpl#getProviderName()}</li>
     *     <li>DatabaseBased ({@link LargeFilePersistenceServiceJpaImpl#getProviderName()}</li>
     * </ul>
     */
    public String defaultLargeFileProviderName;

    public Class<? extends LargeFilePersistenceProvider> getDefaultLargeFileProviderClass() {
        return defaultLargeFileProviderClass;
    }

    public void setDefaultLargeFileProviderClass(Class<? extends LargeFilePersistenceProvider> defaultLargeFileProviderClass) {
        this.defaultLargeFileProviderClass = defaultLargeFileProviderClass;
    }

    public String getDefaultLargeFileProviderName() {
        return defaultLargeFileProviderName;
    }

    public void setDefaultLargeFileProviderName(String defaultLargeFileProviderName) {
        this.defaultLargeFileProviderName = defaultLargeFileProviderName;
    }
}
