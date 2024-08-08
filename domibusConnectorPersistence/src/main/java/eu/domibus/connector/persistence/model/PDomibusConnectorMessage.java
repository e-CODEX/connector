/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model;

import eu.domibus.connector.domain.enums.MessageTargetSource;
import eu.domibus.connector.persistence.model.converter.ZonedDateTimeToTimestampJpaConverter;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
