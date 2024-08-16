/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.persistence.spring;

/**
 * This interface defines constants for persistence profiles.
 */
public interface PersistenceProfiles {
    String STORAGE_FS_PROFILE_NAME = "storage-fs";
    String STORAGE_DB_PROFILE_NAME = "storage-db";
}
