/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.model;

import eu.ecodex.connector.persistence.model.enums.EvidenceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Represents evidence related to a message in the DomibusConnector system. The evidence can be used
 * to track the delivery of the message to the backend or gateway.
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
