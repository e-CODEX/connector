package eu.domibus.connector.domain.model.builder;

import eu.domibus.connector.domain.model.DomibusConnectorParty;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public final class DomibusConnectorPartyBuilder {
    private String partyId;
    private String partyIdType;
    private String role;

    private DomibusConnectorParty.PartyRoleType roleType;

    private DomibusConnectorPartyBuilder() {
    }

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

    public DomibusConnectorParty build() {
        if (partyId == null) {
            throw new IllegalArgumentException("PartyId is not allowed to be null!");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role is not allowed to be null!");
        }
        return new DomibusConnectorParty(partyId, partyIdType, role, roleType);
    }

    public DomibusConnectorPartyBuilder copyPropertiesFrom(DomibusConnectorParty party) {
        this.partyId = party.getPartyId();
        this.partyIdType = party.getPartyIdType();
        this.role = party.getRole();
        this.roleType = party.getRoleType();
        return this;
    }
}
