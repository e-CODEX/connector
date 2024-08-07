package eu.domibus.connector.persistence.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name = PDomibusConnectorMessageInfo.TABLE_NAME)
public class PDomibusConnectorMessageInfo {

    public static final String TABLE_NAME = "DOMIBUS_CONNECTOR_MESSAGE_INFO";

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MESSAGE_ID", nullable = false)
    private PDomibusConnectorMessage message;

    @ManyToOne
    @JoinColumn(name = "FK_FROM_PARTY_ID", referencedColumnName = "ID")
    private PDomibusConnectorParty from;

    @ManyToOne
    @JoinColumn(name = "FK_TO_PARTY_ID", referencedColumnName = "ID")
    private PDomibusConnectorParty to;

    @Column(name = "ORIGINAL_SENDER", length = 2048)
    private String originalSender;

    @Column(name = "FINAL_RECIPIENT", length = 2048)
    private String finalRecipient;

    @ManyToOne
    @JoinColumn(name = "FK_SERVICE")
    private PDomibusConnectorService service;

    @ManyToOne
    @JoinColumn(name = "FK_ACTION")
    private PDomibusConnectorAction action;

    @Column(name = "CREATED", nullable = false)
    private Date created;

    @Column(name = "UPDATED", nullable = false)
    private Date updated;

    @PrePersist
    public void prePersist() {
        this.updated = new Date();
        this.created = new Date();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updated = new Date();
    }
        
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PDomibusConnectorMessage getMessage() {
        return message;
    }

    public void setMessage(PDomibusConnectorMessage message) {
        this.message = message;
    }

    public PDomibusConnectorParty getFrom() {
        return from;
    }

    public void setFrom(PDomibusConnectorParty from) {
        this.from = from;
    }

    public PDomibusConnectorParty getTo() {
        return to;
    }

    public void setTo(PDomibusConnectorParty to) {
        this.to = to;
    }

    public String getOriginalSender() {
        return originalSender;
    }

    public void setOriginalSender(String originalSender) {
        this.originalSender = originalSender;
    }

    public String getFinalRecipient() {
        return finalRecipient;
    }

    public void setFinalRecipient(String finalRecipient) {
        this.finalRecipient = finalRecipient;
    }

    public PDomibusConnectorService getService() {
        return service;
    }

    public void setService(PDomibusConnectorService service) {
        this.service = service;
    }

    public PDomibusConnectorAction getAction() {
        return action;
    }

    public void setAction(PDomibusConnectorAction action) {
        this.action = action;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
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
        toString.append("id", id);
        toString.append("finalRecipient", this.finalRecipient);
        toString.append("originalSender", this.originalSender);
        toString.append("fromParty", this.from);
        toString.append("toParty", this.to);
        toString.append("service", this.service);
        toString.append("action", this.action);
        return toString.build();
    }

}
