/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service;

import eu.domibus.connector.domain.model.DomibusConnectorKeystore;

/**
 * This interface provides methods to interact with the Domibus Connector Keystore persistence.
 */
@SuppressWarnings("checkstyle:ParameterName")
public interface DomibusConnectorKeystorePersistenceService {
    DomibusConnectorKeystore getKeystoreByUUID(String uuid);

    DomibusConnectorKeystore persistNewKeystore(DomibusConnectorKeystore pKeystore);

    void updateKeystorePassword(DomibusConnectorKeystore pKeystore, String newKeystorePassword);
}
