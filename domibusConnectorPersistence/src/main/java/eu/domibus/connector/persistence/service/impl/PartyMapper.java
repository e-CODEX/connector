/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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
