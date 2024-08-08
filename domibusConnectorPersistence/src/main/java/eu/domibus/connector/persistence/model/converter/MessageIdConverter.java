/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model.converter;

import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * The MessageIdConverter class is an implementation of the AttributeConverter interface, providing
 * conversion between DomibusConnectorMessageId and String types.
 *
 * <p>The class is annotated with @Converter to indicate that it should be automatically applied
 * for entity attribute conversions.
 */
@Converter(autoApply = true)
public class MessageIdConverter implements AttributeConverter<DomibusConnectorMessageId, String> {
    @Override
    public String convertToDatabaseColumn(DomibusConnectorMessageId attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getConnectorMessageId();
    }

    @Override
    public DomibusConnectorMessageId convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return new DomibusConnectorMessageId(dbData);
    }
}
