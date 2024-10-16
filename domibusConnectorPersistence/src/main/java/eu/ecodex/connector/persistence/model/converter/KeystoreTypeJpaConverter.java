/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.model.converter;

import eu.ecodex.connector.domain.model.DomibusConnectorKeystore.KeystoreType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts the KeystoreType enum to a database column and vice versa.
 */
@Converter(autoApply = true)
public class KeystoreTypeJpaConverter implements AttributeConverter<KeystoreType, String> {
    @Override
    public String convertToDatabaseColumn(KeystoreType attribute) {
        return attribute.getDbName();
    }

    @Override
    public KeystoreType convertToEntityAttribute(String dbData) {
        return KeystoreType.ofDbName(dbData);
    }
}
