/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.largefiles.provider;

import static eu.ecodex.connector.persistence.spring.PersistenceProfiles.STORAGE_FS_PROFILE_NAME;

import eu.ecodex.connector.persistence.dao.CommonPersistenceTest;
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
