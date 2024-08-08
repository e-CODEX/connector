/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model.converter;

import eu.domibus.connector.domain.enums.TransportState;
import javax.persistence.AttributeConverter;

/**
 * The TransportStateJpaConverter class is responsible for converting the TransportState enum to a
 * database column value and vice versa.
 */
public class TransportStateJpaConverter
    implements AttributeConverter<TransportState, java.lang.String> {
    public static final TransportStateJpaConverter converter = new TransportStateJpaConverter();

    @Override
    public java.lang.String convertToDatabaseColumn(TransportState attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDbName();
    }

    @Override
    public TransportState convertToEntityAttribute(java.lang.String dbData) {
        if (dbData == null) {
            return null;
        }
        return TransportState.ofDbName(dbData);
    }
}
