/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model.converter;

import eu.domibus.connector.domain.model.DomibusConnectorParty.PartyRoleType;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * The PartyRoleTypeJpaConverter class is a JPA converter that converts between PartyRoleType
 * objects and String values for database storage. It is used to persist the PartyRoleType enum in
 * the database.
 */
@Converter(autoApply = true)
public class PartyRoleTypeJpaConverter implements AttributeConverter<PartyRoleType, String> {
    @Override
    public String convertToDatabaseColumn(PartyRoleType attribute) {
        return attribute.getDbName();
    }

    @Override
    public PartyRoleType convertToEntityAttribute(String dbData) {
        return PartyRoleType.ofDbName(dbData);
    }
}
