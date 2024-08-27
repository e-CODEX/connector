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

import eu.domibus.connector.domain.enums.TransportState;
import jakarta.persistence.AttributeConverter;

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
