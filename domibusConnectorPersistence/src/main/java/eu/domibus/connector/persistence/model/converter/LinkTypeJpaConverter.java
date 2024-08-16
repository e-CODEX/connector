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
