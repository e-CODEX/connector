/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model.converter;

import eu.domibus.connector.domain.enums.MessageTargetSource;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Converts the MessageTargetSource enum value to a String for database storage and vice versa.
 * This converter is applied automatically by JPA.
 */
@Converter(autoApply = true)
public class MessageTargetSourceJpaConverter
    implements AttributeConverter<MessageTargetSource, String> {
    @Override
    public String convertToDatabaseColumn(MessageTargetSource attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDbName();
    }

    @Override
    public MessageTargetSource convertToEntityAttribute(String dbData) {
        return MessageTargetSource.ofOfDbName(dbData);
    }
}
