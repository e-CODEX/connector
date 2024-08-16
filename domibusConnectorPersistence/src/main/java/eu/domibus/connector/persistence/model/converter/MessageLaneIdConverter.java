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

import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * The MessageLaneIdConverter class is a converter class used to convert the custom attribute
 * BusinessDomainId to a String when storing it in the database and converting it back to
 * BusinessDomainId when retrieving it from the database.
 *
 * <p>This class implements the AttributeConverter interface and is annotated with @Converter to
 * make it automatically applicable to all attributes of type BusinessDomainId.
 *
 * @since 1.0
 */
@Converter(autoApply = true)
public class MessageLaneIdConverter
    implements AttributeConverter<DomibusConnectorBusinessDomain.BusinessDomainId, String> {
    @Override
    public String convertToDatabaseColumn(
        DomibusConnectorBusinessDomain.BusinessDomainId attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getMessageLaneId();
    }

    @Override
    public DomibusConnectorBusinessDomain.BusinessDomainId convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return new DomibusConnectorBusinessDomain.BusinessDomainId(dbData);
    }
}
