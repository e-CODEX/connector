/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.DomibusConnectorParty;
import eu.domibus.connector.persistence.model.PDomibusConnectorParty;
import javax.annotation.Nullable;
import lombok.experimental.UtilityClass;

/**
 * The PartyMapper class provides static methods for mapping between the
 * {@link PDomibusConnectorParty} and {@link DomibusConnectorParty} classes.
 */
@UtilityClass
public class PartyMapper {
    static @Nullable
    DomibusConnectorParty mapPartyToDomain(@Nullable PDomibusConnectorParty persistenceParty) {
        if (persistenceParty != null) {
            var p = new eu.domibus.connector.domain.model.DomibusConnectorParty(
                persistenceParty.getPartyId(),
                persistenceParty.getPartyIdType(),
                persistenceParty.getRole()
            );
            p.setPartyName(persistenceParty.getPmodePartyIdentifier());
            p.setRoleType(persistenceParty.getRoleType());
            p.setDbKey(persistenceParty.getId());
            return p;
        }
        return null;
    }

    static @Nullable
    PDomibusConnectorParty mapPartyToPersistence(@Nullable DomibusConnectorParty party) {
        if (party != null) {
            var persistenceParty = new PDomibusConnectorParty();
            persistenceParty.setPartyId(party.getPartyId());
            persistenceParty.setPartyIdType(party.getPartyIdType());
            persistenceParty.setRole(party.getRole());
            persistenceParty.setPmodePartyIdentifier(party.getPartyName());
            persistenceParty.setRoleType(party.getRoleType());
            persistenceParty.setId(party.getDbKey());
            return persistenceParty;
        }
        return null;
    }
}
