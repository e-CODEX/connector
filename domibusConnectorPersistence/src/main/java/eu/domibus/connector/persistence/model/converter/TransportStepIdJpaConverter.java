/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model.converter;

import eu.domibus.connector.controller.service.TransportStateService.TransportId;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * The TransportStepIdJpaConverter class is a JPA converter used to convert TransportId objects to
 * and from their corresponding database representation as strings.
 *
 * <p>When converting from TransportId to database column, the convertToDatabaseColumn method is
 * called. If the attribute is null, null is returned. Otherwise, the transport ID is converted to
 * its string representation and returned. When converting from database column to TransportId, the
 * convertToEntityAttribute method is called. If the database data is null, null is returned.
 * Otherwise, a new TransportId object created with the database data as the transport identifier
 * and returned.
 */
@Converter(autoApply = true)
public class TransportStepIdJpaConverter implements AttributeConverter<TransportId, String> {
    @Override
    public String convertToDatabaseColumn(TransportId attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getTransportId();
    }

    @Override
    public TransportId convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return new TransportId(dbData);
    }
}
