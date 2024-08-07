package eu.domibus.connector.persistence.model;

import eu.domibus.connector.persistence.model.enums.EvidenceType;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

import jakarta.persistence.*;


@Entity
@Table(name = PDomibusConnectorEvidence.TABLE_NAME)
public class PDomibusConnectorEvidence {

    public static final String TABLE_NAME = "DOMIBUS_CONNECTOR_EVIDENCE";

    @Id
    @Column(name="ID")
    @TableGenerator(name = "seq" + TABLE_NAME,
            table = PDomibusConnectorPersistenceModel.SEQ_STORE_TABLE_NAME,
            pkColumnName = PDomibusConnectorPersistenceModel.SEQ_NAME_COLUMN_NAME,
            pkColumnValue = TABLE_NAME + ".ID",
            valueColumnName = PDomibusConnectorPersistenceModel.SEQ_VALUE_COLUMN_NAME,
            initialValue = PDomibusConnectorPersistenceModel.INITIAL_VALUE,
            allocationSize = PDomibusConnectorPersistenceModel.ALLOCATION_SIZE_BULK)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seq" + TABLE_NAME)
    private Long id;

    /**
     * the message this evidence is referencing
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MESSAGE_ID", nullable = false)
    private PDomibusConnectorMessage businessMessage;


    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private EvidenceType type;

    @Lob
    @Column(name = "EVIDENCE")
    private String evidence;

    @Column(name = "DELIVERED_NAT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deliveredToBackend;

    @Column(name = "DELIVERED_GW")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deliveredToGateway;

    @Column(name = "UPDATED", nullable = false)
    private Date updated;

    @PrePersist
    public void prePersist() {
        updated = new Date();
    }
    
    @PreUpdate
    public void preUpdate() {
        updated = new Date();
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PDomibusConnectorMessage getBusinessMessage() {
        return businessMessage;
    }

    public void setBusinessMessage(PDomibusConnectorMessage message) {
        this.businessMessage = message;
    }

    public EvidenceType getType() {
        return type;
    }

    public void setType(EvidenceType type) {
        this.type = type;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    public Date getDeliveredToBackend() {
        return deliveredToBackend;
    }

    public void setDeliveredToBackend(Date deliveredToNationalSystem) {
        this.deliveredToBackend = deliveredToNationalSystem;
    }

    public Date getDeliveredToGateway() {
        return deliveredToGateway;
    }

    public void setDeliveredToGateway(Date deliveredToGateway) {
        this.deliveredToGateway = deliveredToGateway;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        ToStringBuilder toString = new ToStringBuilder(this);
        toString.append("id", this.id);
        toString.append("evidenceType", this.type);
        toString.append("evidence", this.evidence);
        toString.append("businessMessage", this.businessMessage.getConnectorMessageId());
        return toString.build();
    }
}
