package eu.domibus.connector.domain.model;

import org.springframework.core.style.ToStringCreator;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;


/**
 * @author riederb
 * @version 1.0
 */
public class DomibusConnectorParty implements Serializable {
    private Long dbKey;
    private String partyName;
    private String partyId;
    private String partyIdType;
    private String role;
    private PartyRoleType roleType;
    /**
     * Default constructor, needed for frameworks
     * to serialize and deserialize objects of this class
     */
    public DomibusConnectorParty() {
    }

    /**
     * DomibusConnectorMessageDocument
     *
     * @param partyId     partyId
     * @param partyIdType partyIdType
     * @param role        role
     */
    public DomibusConnectorParty(final String partyId, final String partyIdType, final String role) {
        this.partyId = partyId;
        this.partyIdType = partyIdType;
        this.role = role;
    }

    /**
     * DomibusConnectorMessageDocument
     *
     * @param partyId     partyId
     * @param partyIdType partyIdType
     * @param roleType    roleType
     */
    public DomibusConnectorParty(final String partyId, final String partyIdType, final PartyRoleType roleType) {
        this.partyId = partyId;
        this.partyIdType = partyIdType;
        this.roleType = roleType;
    }

    /**
     * DomibusConnectorMessageDocument
     *
     * @param partyId     partyId
     * @param partyIdType partyIdType
     * @param role        role
     * @param roleType    roleType
     */
    public DomibusConnectorParty(
            final String partyId,
            final String partyIdType,
            final String role,
            final PartyRoleType roleType) {
        this.partyId = partyId;
        this.partyIdType = partyIdType;
        this.role = role;
        this.roleType = roleType;
    }

    public Long getDbKey() {
        return dbKey;
    }

    public void setDbKey(Long dbKey) {
        this.dbKey = dbKey;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getPartyId() {
        return this.partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getPartyIdType() {
        return this.partyIdType;
    }

    public void setPartyIdType(String partyIdType) {
        this.partyIdType = partyIdType;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public PartyRoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(PartyRoleType roleType) {
        this.roleType = roleType;
    }

    @Override
    public int hashCode() {
        int result = partyId != null ? partyId.hashCode() : 0;
        result = 31 * result + (partyIdType != null ? partyIdType.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (roleType != null ? roleType.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomibusConnectorParty)) return false;

        DomibusConnectorParty that = (DomibusConnectorParty) o;

        if (!Objects.equals(partyId, that.partyId)) return false;
        if (!Objects.equals(partyIdType, that.partyIdType)) return false;
        if (!Objects.equals(roleType, that.roleType)) return false;
        return Objects.equals(role, that.role);
    }

    @Override
    public String toString() {
        ToStringCreator builder = new ToStringCreator(this);
        builder.append("partyId", this.partyId);
        builder.append("partyIdType", this.partyIdType);
        builder.append("role", this.role);
        builder.append("roleType", this.roleType);
        return builder.toString();
    }

    public enum PartyRoleType {
        INITIATOR("initiator"),
        RESPONDER("responder");

        final String dbName;

        PartyRoleType(String dbName) {
            this.dbName = dbName;
        }

        public static PartyRoleType ofDbName(String dbName) {
            return Stream.of(PartyRoleType.values())
                         .filter(l -> l.dbName.equalsIgnoreCase(dbName))
                         .findFirst().orElse(null);
        }

        public String getDbName() {
            return dbName;
        }

        public String toString() {
            return new ToStringCreator(this)
                    .append("name", this.name())
                    .toString();
        }
    }
}
