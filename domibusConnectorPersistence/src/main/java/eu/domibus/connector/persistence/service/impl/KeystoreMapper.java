/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.DomibusConnectorKeystore;
import eu.domibus.connector.persistence.model.PDomibusConnectorKeystore;
import javax.annotation.Nullable;
import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;

/**
 * This class provides mapping methods to convert objects between the persistence and domain models
 * of the Keystore entity.
 */
@UtilityClass
public class KeystoreMapper {
    static @Nullable DomibusConnectorKeystore mapKeystoreToDomain(
        @Nullable PDomibusConnectorKeystore persistenceKeystore) {
        if (persistenceKeystore != null) {
            return new DomibusConnectorKeystore(
                persistenceKeystore.getUuid(),
                persistenceKeystore.getKeystore(),
                persistenceKeystore.getPassword(),
                persistenceKeystore.getUploaded(),
                persistenceKeystore.getDescription(),
                persistenceKeystore.getType()
            );
        }
        return null;
    }

    static @Nullable PDomibusConnectorKeystore mapKeystoreToPersistence(
        @Nullable DomibusConnectorKeystore keystore) {
        if (keystore != null) {
            var persistenceKeystore = new PDomibusConnectorKeystore();
            BeanUtils.copyProperties(keystore, persistenceKeystore);
            return persistenceKeystore;
        }
        return null;
    }
}
