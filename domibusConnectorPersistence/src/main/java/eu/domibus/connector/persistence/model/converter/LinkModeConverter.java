/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model.converter;

import eu.domibus.connector.domain.enums.LinkMode;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * LinkModeConverter is a class that implements the AttributeConverter interface and is used to
 * convert LinkMode enum values to and from their corresponding database representation.
 *
 * <p>This class is annotated with autoApply=true, which means that the conversion will be applied
 * to all entities that have a LinkMode attribute.
 */
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
