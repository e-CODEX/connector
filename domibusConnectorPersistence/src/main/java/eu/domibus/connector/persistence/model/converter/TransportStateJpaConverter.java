package eu.domibus.connector.persistence.model.converter;

import eu.domibus.connector.domain.enums.TransportState;

import javax.persistence.AttributeConverter;

public class TransportStateJpaConverter implements AttributeConverter<TransportState, java.lang.String> {

    public static TransportStateJpaConverter converter = new TransportStateJpaConverter();

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
