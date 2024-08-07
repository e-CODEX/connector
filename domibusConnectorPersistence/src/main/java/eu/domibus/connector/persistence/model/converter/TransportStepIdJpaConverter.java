package eu.domibus.connector.persistence.model.converter;

import eu.domibus.connector.controller.service.TransportStateService.TransportId;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TransportStepIdJpaConverter implements AttributeConverter<TransportId, String> {

    @Override
    public String convertToDatabaseColumn(TransportId attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getTransportId().toString();
    }

    @Override
    public TransportId convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return new TransportId(dbData);
    }
}
