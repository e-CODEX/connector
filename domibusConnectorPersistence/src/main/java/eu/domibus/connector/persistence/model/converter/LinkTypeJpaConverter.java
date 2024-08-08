/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model.converter;

import eu.domibus.connector.domain.enums.LinkType;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * The LinkTypeJpaConverter class is a JPA converter used to convert between the LinkType enum and
 * its corresponding database representation in string format.
 *
 * @see AttributeConverter
 * @see LinkType
 */
@Converter(autoApply = true)
public class LinkTypeJpaConverter implements AttributeConverter<LinkType, String> {
    @Override
    public String convertToDatabaseColumn(LinkType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDbName();
    }

    @Override
    public LinkType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return LinkType.ofDbName(dbData);
    }
}
