/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Represents additional information about a message stored in the DOMIBUS_CONNECTOR_MESSAGE_INFO
 * table.
 *
 * <p>The PDomibusConnectorMessageInfo class is used to store additional information about a message
 * in the
 * DOMIBUS_CONNECTOR_MESSAGE_INFO table. It contains properties such as the identifier, message,
 * sender, recipient, original sender, final recipient, service, action, and timestamps.
 *
 * <p>The class provides getters and setters for each property, allowing access to and modification
 * of the information.
 */
@Getter
@Setter
@Entity
@Table(name = PDomibusConnectorMessageInfo.TABLE_NAME)
public class PDomibusConnectorMessageInfo {
    public static final String TABLE_NAME = "DOMIBUS_CONNECTOR_MESSAGE_INFO";
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

    /**
     * Executes before the entity is persisted to the database.
     * This method sets the 'updated' and 'created' fields to the current date.
     * If the 'created' field is already set, it will not be updated.
     *
     * @see PDomibusConnectorMessage
     */
    @PrePersist
    public void prePersist() {
        this.updated = new Date();
        this.created = new Date();
    }

    /**
     * Executes before the entity is updated in the database.
     * This method sets the 'updated' field to the current date.
     *
     * @see PDomibusConnectorMessage
     */
    @PreUpdate
    public void preUpdate() {
        this.updated = new Date();
    }

    @Override
    public String toString() {
        var toString = new ToStringBuilder(this);
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
