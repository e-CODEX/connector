package eu.domibus.connector.persistence.model.converter;

import eu.domibus.connector.domain.enums.LinkType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LinkTypeJpaConverter implements AttributeConverter<LinkType, String> {

    @Override
    public String convertToDatabaseColumn(LinkType attribute) {
        if (attribute == null)
            return null;
        return attribute.getDbName();
    }

    @Override
    public LinkType convertToEntityAttribute(String dbData) {
        if (dbData == null)
                return null;
        return LinkType.ofDbName(dbData);
    }
}
