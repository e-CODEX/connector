package eu.domibus.connector.persistence.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.domibus.connector.domain.model.DomibusConnectorParty;

import jakarta.persistence.*;

@Entity
//@IdClass(PDomibusConnectorPartyPK.class)
@Table(name = PDomibusConnectorParty.TABLE_NAME)
public class PDomibusConnectorParty {

    public static final String TABLE_NAME = "DOMIBUS_CONNECTOR_PARTY";

    @Id
    @Column(name="ID")
    @TableGenerator(name = "seq" + TABLE_NAME,
            table = PDomibusConnectorPersistenceModel.SEQ_STORE_TABLE_NAME,
            pkColumnName = PDomibusConnectorPersistenceModel.SEQ_NAME_COLUMN_NAME,
            pkColumnValue = TABLE_NAME + ".ID",
            valueColumnName = PDomibusConnectorPersistenceModel.SEQ_VALUE_COLUMN_NAME,
            initialValue = PDomibusConnectorPersistenceModel.INITIAL_VALUE,
            allocationSize = PDomibusConnectorPersistenceModel.ALLOCATION_SIZE)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seq" + TABLE_NAME)
    private Long id;

    @Column(name = "IDENTIFIER")
    private String pmodePartyIdentifier;

    @Column(name = "PARTY_ID", nullable = false)
    private String partyId;

    @Column(name = "ROLE")
    private String role;

    @Column(name = "PARTY_ID_TYPE", nullable = false, length = 512)
    private String partyIdType;
    
    @Column(name = "ROLE_TYPE", length = 50)
//    @Enumerated(EnumType.STRING)
    private DomibusConnectorParty.PartyRoleType roleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_PMODE_SET", referencedColumnName = "ID")
    private PDomibusConnectorPModeSet pModeSet;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPmodePartyIdentifier() {
        return pmodePartyIdentifier;
    }

    public void setPmodePartyIdentifier(String pmodePartyIdentifier) {
        this.pmodePartyIdentifier = pmodePartyIdentifier;
    }

    public PDomibusConnectorPModeSet getpModeSet() {
        return pModeSet;
    }

    public void setpModeSet(PDomibusConnectorPModeSet pModeSet) {
        this.pModeSet = pModeSet;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPartyIdType() {
        return partyIdType;
    }

    public void setPartyIdType(String partyIdType) {
        this.partyIdType = partyIdType;
    }

    public DomibusConnectorParty.PartyRoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(DomibusConnectorParty.PartyRoleType roleType) {
        this.roleType = roleType;
    }

	@Override
    public String toString() {
        ToStringBuilder toString = new ToStringBuilder(this);
        toString.append("id", partyId);
        toString.append("role", role);
        toString.append("idType", partyIdType);
        toString.append("roleType", roleType);
        return toString.build();
    }
}
