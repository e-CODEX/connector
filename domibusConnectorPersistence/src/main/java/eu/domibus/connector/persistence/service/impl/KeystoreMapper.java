package eu.domibus.connector.persistence.service.impl;

import javax.annotation.Nullable;

import org.springframework.beans.BeanUtils;

import eu.domibus.connector.domain.model.DomibusConnectorKeystore;
import eu.domibus.connector.persistence.model.PDomibusConnectorKeystore;

public class KeystoreMapper {

	static @Nullable DomibusConnectorKeystore mapKeystoreToDomain(@Nullable PDomibusConnectorKeystore persistenceKeystore) {
        if (persistenceKeystore != null) {
            eu.domibus.connector.domain.model.DomibusConnectorKeystore keystore
                    = new eu.domibus.connector.domain.model.DomibusConnectorKeystore(
                    persistenceKeystore.getUuid(),
                    persistenceKeystore.getKeystore(),
                    persistenceKeystore.getPassword(),
                    persistenceKeystore.getUploaded(),
                    persistenceKeystore.getDescription(),
                    persistenceKeystore.getType()
            );
            return keystore;
        }
        return null;
    }


    static @Nullable PDomibusConnectorKeystore mapKeystoreToPersistence(@Nullable DomibusConnectorKeystore keystore) {
        if (keystore != null) {
        	PDomibusConnectorKeystore persistenceKeystore = new PDomibusConnectorKeystore();
            BeanUtils.copyProperties(keystore, persistenceKeystore);
            return persistenceKeystore;
        }
        return null;
    }

}
