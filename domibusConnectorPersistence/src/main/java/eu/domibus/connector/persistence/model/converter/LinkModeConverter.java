package eu.domibus.connector.persistence.model.converter;

import eu.domibus.connector.domain.enums.LinkMode;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LinkModeConverter implements AttributeConverter<LinkMode, String> {

    @Override
    public String convertToDatabaseColumn(LinkMode attribute) {
        if (attribute == null) {
            return "";
        }
        return attribute.getDbName();
    }

    @Override
    public LinkMode convertToEntityAttribute(String dbData) {
        return LinkMode.ofDbName(dbData).orElse(null);

    }
}
