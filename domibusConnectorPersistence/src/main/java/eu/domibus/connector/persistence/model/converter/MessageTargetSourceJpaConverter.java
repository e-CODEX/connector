/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.persistence.model.converter;

import eu.domibus.connector.domain.enums.MessageTargetSource;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

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
