/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
