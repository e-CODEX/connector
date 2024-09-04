/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.core.style.ToStringCreator;

/**
 * The DomibusConnectorParty class represents a party in the Domibus connector. It contains
 * information about the party's ID, ID type, and role.
 *
 * @author riederb
 * @version 1.0 This class provides various constructors and getter/setter methods for accessing and
 *      modifying the party's properties.
 */
@Data
@NoArgsConstructor
public class DomibusConnectorParty implements Serializable {
    /**
     * The PartyRoleType enum represents the possible types of party roles in a system. Each role
     * type has a corresponding database name associated with it.
     */
    @Getter
    public enum PartyRoleType {
        INITIATOR("initiator"),
        RESPONDER("responder");

        PartyRoleType(String dbName) {
            this.dbName = dbName;
        }

        final String dbName;

        /**
         * Retrieves the PartyRoleType enum constant based on the provided database name.
         *
         * @param dbName the database name associated with a party role type
         * @return the PartyRoleType enum constant with the matching database name, or null if no
         *      match is found
         */
        public static PartyRoleType ofDbName(String dbName) {
            return Stream.of(PartyRoleType.values())
                .filter(l -> l.dbName.equalsIgnoreCase(dbName))
                .findFirst().orElse(null);
        }

        @Override
        public String toString() {
            return new ToStringCreator(this)
                .append("name", this.name())
                .toString();
        }
    }

    private Long dbKey;
    private String partyName;
    private String partyId;
    private String partyIdType;
    private String role;
    private PartyRoleType roleType;

    /**
     * Represents a party in the Domibus Connector.
     *
     * @param partyId      The ID of the party
     * @param partyIdType  The type of the party ID
     * @param role         The role of the party
     */
    public DomibusConnectorParty(final String partyId, final String partyIdType,
                                 final String role) {
        this.partyId = partyId;
        this.partyIdType = partyIdType;
        this.role = role;
    }

    /**
     * Represents a party in the Domibus Connector.
     *
     * @param partyId       The ID of the party
     * @param partyIdType   The type of the party ID
     * @param roleType      The role type of the party
     */
    public DomibusConnectorParty(final String partyId, final String partyIdType,
                                 final PartyRoleType roleType) {
        this.partyId = partyId;
        this.partyIdType = partyIdType;
        this.roleType = roleType;
    }

    /**
     * Represents a party in the Domibus Connector.
     *
     * @param partyId     The ID of the party
     * @param partyIdType The type of the party ID
     * @param role        The role of the party
     * @param roleType    The role type of the party
     */
    public DomibusConnectorParty(final String partyId, final String partyIdType, final String role,
                                 final PartyRoleType roleType) {
        this.partyId = partyId;
        this.partyIdType = partyIdType;
        this.role = role;
        this.roleType = roleType;
    }

    @Override
    public String toString() {
        var builder = new ToStringCreator(this);
        builder.append("partyId", this.partyId);
        builder.append("partyIdType", this.partyIdType);
        builder.append("role", this.role);
        builder.append("roleType", this.roleType);
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DomibusConnectorParty that)) {
            return false;
        }

        if (!Objects.equals(partyId, that.partyId)) {
            return false;
        }
        if (!Objects.equals(partyIdType, that.partyIdType)) {
            return false;
        }
        if (!Objects.equals(roleType, that.roleType)) {
            return false;
        }
        return Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        int result = partyId != null ? partyId.hashCode() : 0;
        result = 31 * result + (partyIdType != null ? partyIdType.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (roleType != null ? roleType.hashCode() : 0);
        return result;
    }
}
