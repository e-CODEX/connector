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

import eu.domibus.connector.domain.enums.LinkMode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

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
