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

import jakarta.persistence.AttributeConverter;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * A converter class that converts ZonedDateTime objects to Timestamp objects for JPA persistence.
 */
public class ZonedDateTimeToTimestampJpaConverter
    implements AttributeConverter<ZonedDateTime, java.sql.Timestamp> {
    @Override
    public Timestamp convertToDatabaseColumn(ZonedDateTime attribute) {
        if (attribute == null) {
            return null;
        }
        return Timestamp.from(attribute.toInstant());
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(Timestamp dbData) {
        if (dbData == null) {
            return null;
        }
        return ZonedDateTime.ofInstant(dbData.toInstant(), ZoneId.systemDefault());
    }
}
