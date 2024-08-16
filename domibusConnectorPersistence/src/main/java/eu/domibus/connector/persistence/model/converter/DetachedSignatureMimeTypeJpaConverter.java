/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
