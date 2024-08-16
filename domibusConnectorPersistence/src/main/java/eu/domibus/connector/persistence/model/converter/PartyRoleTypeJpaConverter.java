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
