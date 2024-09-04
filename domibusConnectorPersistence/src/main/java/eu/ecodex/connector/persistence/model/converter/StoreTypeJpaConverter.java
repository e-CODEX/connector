/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.model.converter;

import eu.ecodex.connector.persistence.service.impl.helper.StoreType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

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
