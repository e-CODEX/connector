/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.persistence.model;

import eu.domibus.connector.domain.enums.MessageTargetSource;
import eu.domibus.connector.persistence.model.converter.ZonedDateTimeToTimestampJpaConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.style.ToStringCreator;

/**
 * Represents a message stored in the DOMIBUS_CONNECTOR_MESSAGE table.
 *
 * <p>The PDomibusConnectorMessage class is used to store information about a message in the
 * DOMIBUS_CONNECTOR_MESSAGE table. It contains properties such as the identifier, EBMS message ID,
 * backend message ID, backend name, gateway name, connector message ID, conversation ID, direction
 * source, direction target, hash value, and various timestamps.
 *
 * <p>This class also has a reference to the PDomibusConnectorMessageInfo class which contains
 * additional information about the message. In addition, there is a set of
 * PDomibusConnectorEvidence objects that are related to the message as well as a set of transported
 * evidences.
 */
@Getter
@Setter
@Entity
@Table(name = PDomibusConnectorMessage.TABLE_NAME)
public class PDomibusConnectorMessage implements Serializable {
    public static final String TABLE_NAME = "DOMIBUS_CONNECTOR_MESSAGE";
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
    @Column(name = "EBMS_MESSAGE_ID", unique = true)
    private String ebmsMessageId;
    @Column(name = "BACKEND_MESSAGE_ID", unique = true)
    private String backendMessageId;
    @Column(name = "BACKEND_NAME")
    private String backendName;
    @Column(name = "GATEWAY_NAME")
    private String gatewayName;
    @Column(name = "CONNECTOR_MESSAGE_ID", unique = true, nullable = false, length = 255)
    private String connectorMessageId;
    @Column(name = "CONVERSATION_ID")
    private String conversationId;
    @Column(name = "DIRECTION_SOURCE", length = 20)
    private MessageTargetSource directionSource;
    @Column(name = "DIRECTION_TARGET", length = 20)
    private MessageTargetSource directionTarget;
    @Lob
    @Column(name = "HASH_VALUE")
    private String hashValue;
    @Column(name = "DELIVERED_BACKEND")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deliveredToNationalSystem;
    @Column(name = "DELIVERED_GW")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deliveredToGateway;
    @Convert(converter = ZonedDateTimeToTimestampJpaConverter.class)
    @Column(name = "CONFIRMED")
    //    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime confirmed;
    @Convert(converter = ZonedDateTimeToTimestampJpaConverter.class)
    @Column(name = "REJECTED")
    //    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime rejected;
    @Column(name = "UPDATED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
    @Column(name = "CREATED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @OneToOne(mappedBy = "message", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private PDomibusConnectorMessageInfo messageInfo;
    /**
     * This messages here are related to the BusinessMessage.
     */
    @OneToMany(mappedBy = "businessMessage", fetch = FetchType.EAGER)
    private Set<PDomibusConnectorEvidence> relatedEvidences = new HashSet<>();

    /**
     * This evidences here are transported with the message.
     */
    @PrePersist
    public void prePersist() {
        this.updated = new Date();
        if (this.created == null) {
            this.created = this.updated;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updated = new Date();
    }

    @Override
    public String toString() {
        ToStringCreator builder = new ToStringCreator(this)
            .append("dbId", this.id)
            .append("connectorMessageId", this.connectorMessageId)
            .append("ebmsId", this.ebmsMessageId)
            .append("backendMessageId", this.backendMessageId);
        return builder.toString();
    }
}
