package eu.domibus.connector.persistence.service;

import eu.domibus.connector.domain.model.DomibusConnectorKeystore;


public interface DomibusConnectorKeystorePersistenceService {
    // DomibusConnectorKeystore persistNewKeystore(String uuid, byte[] keystoreBytes, String password,
    // String description, DomibusConnectorKeystore.KeystoreType type);

    DomibusConnectorKeystore getKeystoreByUUID(String uuid);

    DomibusConnectorKeystore persistNewKeystore(DomibusConnectorKeystore pKeystore);

    void updateKeystorePassword(DomibusConnectorKeystore pKeystore, String newKeystorePassword);
}
