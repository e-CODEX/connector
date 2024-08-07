package eu.domibus.connector.persistence.model.converter;

import eu.domibus.connector.domain.enums.MessageTargetSource;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.lang.annotation.Annotation;

@Converter(autoApply = true)
public class MessageTargetSourceJpaConverter implements AttributeConverter<MessageTargetSource, String> {


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
