/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model.converter;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javax.persistence.AttributeConverter;

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
