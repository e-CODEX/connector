/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.domain.model.builder;

import eu.domibus.connector.domain.model.DomibusConnectorParty;
import lombok.NoArgsConstructor;

/**
 * The DomibusConnectorPartyBuilder class is used to create instances of the DomibusConnectorParty
 * class. It provides methods for setting the properties of the party and building the final
 * instance.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@NoArgsConstructor
public final class DomibusConnectorPartyBuilder {
    private String partyId;
    private String partyIdType;
    private String role;
    private DomibusConnectorParty.PartyRoleType roleType;

    public static DomibusConnectorPartyBuilder createBuilder() {
        return new DomibusConnectorPartyBuilder();
    }

    public DomibusConnectorPartyBuilder setPartyId(String partyId) {
        this.partyId = partyId;
        return this;
    }

    public DomibusConnectorPartyBuilder setPartyIdType(String partyIdType) {
        this.partyIdType = partyIdType;
        return this;
    }

    public DomibusConnectorPartyBuilder setRole(String role) {
        this.role = role;
        return this;
    }

    public DomibusConnectorPartyBuilder setRoleType(DomibusConnectorParty.PartyRoleType role) {
        this.roleType = role;
        return this;
    }

    /**
     * Builds a DomibusConnectorParty instance with the provided properties.
     *
     * @return a new instance of DomibusConnectorParty
     * @throws IllegalArgumentException if partyId or role is null
     */
    public DomibusConnectorParty build() {
        if (partyId == null) {
            throw new IllegalArgumentException("PartyId is not allowed to be null!");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role is not allowed to be null!");
        }
        return new DomibusConnectorParty(partyId, partyIdType, role, roleType);
    }

    /**
     * Copies the properties from the given DomibusConnectorParty to the current
     * DomibusConnectorPartyBuilder instance.
     *
     * @param party the DomibusConnectorParty to copy properties from
     * @return the current DomibusConnectorPartyBuilder instance
     * @throws IllegalArgumentException if the party is null
     */
    public DomibusConnectorPartyBuilder copyPropertiesFrom(DomibusConnectorParty party) {
        this.partyId = party.getPartyId();
        this.partyIdType = party.getPartyIdType();
        this.role = party.getRole();
        this.roleType = party.getRoleType();
        return this;
    }
}
