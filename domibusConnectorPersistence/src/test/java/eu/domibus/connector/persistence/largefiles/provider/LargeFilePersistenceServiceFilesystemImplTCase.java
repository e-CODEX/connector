package eu.domibus.connector.persistence.largefiles.provider;

import static eu.domibus.connector.persistence.spring.PersistenceProfiles.STORAGE_FS_PROFILE_NAME;

import eu.domibus.connector.persistence.dao.CommonPersistenceTest;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.FileSystemUtils;

/**
 * Represents a test case for the LargeFilePersistenceServiceFilesystemImpl class.
 */
@CommonPersistenceTest
@ActiveProfiles({"test", "db_h2", "connector", STORAGE_FS_PROFILE_NAME})
class LargeFilePersistenceServiceFilesystemImplTCase
    extends CommonLargeFilePersistenceProviderITCase {
    @BeforeAll
    public static void deleteFS() {
        FileSystemUtils.deleteRecursively(Paths.get("./target/ittest").toFile());
    }

    @Override
    protected String getProviderName() {
        return LargeFilePersistenceServiceFilesystemImpl.PROVIDER_NAME;
    }
}
