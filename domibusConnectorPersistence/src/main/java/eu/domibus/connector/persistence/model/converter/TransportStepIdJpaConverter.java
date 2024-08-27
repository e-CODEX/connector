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

import eu.domibus.connector.controller.service.TransportStateService.TransportId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

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
