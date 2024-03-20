package eu.domibus.connector.persistence.model.converter;

import javax.persistence.AttributeConverter;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ZonedDateTimeToTimestampJpaConverter implements AttributeConverter<ZonedDateTime, java.sql.Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(ZonedDateTime attribute) {
        if (attribute == null) return null;
        return Timestamp.from(attribute.toInstant());
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(Timestamp dbData) {
        if (dbData == null) return null;
        return ZonedDateTime.ofInstant(dbData.toInstant(), ZoneId.systemDefault());
    }

}
