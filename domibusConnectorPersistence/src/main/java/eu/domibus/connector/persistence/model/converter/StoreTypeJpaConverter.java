/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model.converter;

import eu.domibus.connector.persistence.service.impl.helper.StoreType;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * The StoreTypeJpaConverter class is a JPA converter that converts between the StoreType enum and
 * its corresponding database String representation.
 */
@Converter(autoApply = true)
public class StoreTypeJpaConverter implements AttributeConverter<StoreType, String> {
    @Override
    public String convertToDatabaseColumn(StoreType attribute) {
        return attribute.getDbString();
    }

    @Override
    public StoreType convertToEntityAttribute(String dbData) {
        return StoreType.fromDbName(dbData);
    }
}
