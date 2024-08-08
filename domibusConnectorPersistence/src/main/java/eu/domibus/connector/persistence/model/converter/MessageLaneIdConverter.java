/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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
