/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model.converter;

import eu.domibus.connector.domain.model.DetachedSignatureMimeType;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * The DetachedSignatureMimeTypeJpaConverter class is a JPA converter that converts
 * DetachedSignatureMimeType objects to and from their corresponding database representation
 * as strings.
 */
@Converter(autoApply = true)
public class DetachedSignatureMimeTypeJpaConverter
    implements AttributeConverter<DetachedSignatureMimeType, String> {
    @Override
    public String convertToDatabaseColumn(DetachedSignatureMimeType attribute) {
        return attribute.getCode();
    }

    @Override
    public DetachedSignatureMimeType convertToEntityAttribute(String dbData) {
        return DetachedSignatureMimeType.fromCode(dbData);
    }
}
