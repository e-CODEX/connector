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

import eu.ecodex.connector.domain.model.DomibusConnectorMessageId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

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
