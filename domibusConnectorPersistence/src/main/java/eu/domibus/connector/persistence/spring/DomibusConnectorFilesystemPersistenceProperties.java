package eu.domibus.connector.persistence.spring;

import eu.domibus.connector.persistence.largefiles.provider.LargeFilePersistenceServiceFilesystemImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@ConfigurationProperties(prefix = "connector.persistence.filesystem")
@Validated
public class DomibusConnectorFilesystemPersistenceProperties {


    /**
     * Property to configure the storage location on filesystem
     *  under this folder the {@link LargeFilePersistenceServiceFilesystemImpl}
     *  is managing the data
     */
    @NotNull
    private Path storagePath = Paths.get("./data/fsstorage");

    /**
     * Should the written files be encrypted?
     * Default is yes
     */
    private boolean encryptionActive = true;

    /**
     * Should the directory be created if it does not exist?
     */
    private boolean createDir = true;

    public Path getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(Path storagePath) {
        this.storagePath = storagePath;
    }

    public boolean isEncryptionActive() {
        return encryptionActive;
    }

    public void setEncryptionActive(boolean encryptionActive) {
        this.encryptionActive = encryptionActive;
    }

    public boolean isCreateDir() {
        return createDir;
    }

    public void setCreateDir(boolean createDir) {
        this.createDir = createDir;
    }
}
