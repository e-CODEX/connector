package eu.domibus.connector.persistence.spring;

import eu.domibus.connector.persistence.largefiles.provider.LargeFilePersistenceServiceFilesystemImpl;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * This class represents the configuration properties for the Domibus Connector Filesystem
 * Persistence. It is used to configure the storage location, encryption and directory creation
 * options.
 */
@Component
@ConfigurationProperties(prefix = "connector.persistence.filesystem")
@Validated
@Getter
@Setter
public class DomibusConnectorFilesystemPersistenceProperties {
    /**
     * Property to configure the storage location on filesystem under this folder the
     * {@link LargeFilePersistenceServiceFilesystemImpl} is managing the data.
     */
    @NotNull
    private Path storagePath = Paths.get("./data/fsstorage");
    /**
     * Should the written files be encrypted? Default is yes.
     */
    private boolean encryptionActive = true;
    /**
     * Should the directory be created if it does not exist.
     */
    private boolean createDir = true;
}
