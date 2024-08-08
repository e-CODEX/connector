/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model;

import eu.domibus.connector.persistence.model.enums.EvidenceType;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Represents evidence related to a message in the DomibusConnector system.
 * The evidence can be used to track the delivery of the message to the backend or gateway.
 */
@Getter
@Setter
@Entity
@Table(name = PDomibusConnectorEvidence.TABLE_NAME)
public class PDomibusConnectorEvidence {
    public static final String TABLE_NAME = "DOMIBUS_CONNECTOR_EVIDENCE";
    @Id
    @Column(name = "ID")
    @TableGenerator(
        name = "seq" + TABLE_NAME,
        table = PDomibusConnectorPersistenceModel.SEQ_STORE_TABLE_NAME,
        pkColumnName = PDomibusConnectorPersistenceModel.SEQ_NAME_COLUMN_NAME,
        pkColumnValue = TABLE_NAME + ".ID",
        valueColumnName = PDomibusConnectorPersistenceModel.SEQ_VALUE_COLUMN_NAME,
        initialValue = PDomibusConnectorPersistenceModel.INITIAL_VALUE,
        allocationSize = PDomibusConnectorPersistenceModel.ALLOCATION_SIZE_BULK
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seq" + TABLE_NAME)
    private Long id;
    /**
     * The message this evidence is referencing.
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

    @Override
    public String toString() {
        var toString = new ToStringBuilder(this);
        toString.append("id", this.id);
        toString.append("evidenceType", this.type);
        toString.append("evidence", this.evidence);
        toString.append("businessMessage", this.businessMessage.getConnectorMessageId());
        return toString.build();
    }
}
