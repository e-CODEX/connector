/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model.converter;

import eu.domibus.connector.domain.model.DomibusConnectorKeystore.KeystoreType;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

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
