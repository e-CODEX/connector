package eu.domibus.connector.persistence.model.converter;

import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MessageLaneIdConverter implements AttributeConverter<DomibusConnectorBusinessDomain.BusinessDomainId, String> {

    @Override
    public String convertToDatabaseColumn(DomibusConnectorBusinessDomain.BusinessDomainId attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getMessageLaneId();
    }

    @Override
    public DomibusConnectorBusinessDomain.BusinessDomainId convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return new DomibusConnectorBusinessDomain.BusinessDomainId(dbData);
    }
}
