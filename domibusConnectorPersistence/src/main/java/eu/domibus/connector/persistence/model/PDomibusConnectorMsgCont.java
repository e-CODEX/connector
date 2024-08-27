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

import eu.domibus.connector.persistence.service.impl.helper.StoreType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * This class stores message content metadata for.
 * <ul>
 *    <li>message attachments</li>
 *    <li>message content xml</li>
 *    <li>message content document</li>
 * </ul>
 *
 * <p>The storage itself is delegated to a storage provider
 *  the name and reference of the storage provider is also stored
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Getter
@Setter
@Entity
@Table(name = "DOMIBUS_CONNECTOR_MSG_CONT")
public class PDomibusConnectorMsgCont {
    public static final String TABLE_NAME = "DOMIBUS_CONNECTOR_MSG_CONT";
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
    @Column(name = "STORAGE_PROVIDER_NAME")
    private String storageProviderName;
    @Column(name = "STORAGE_REFERENCE_ID", length = 512)
    private String storageReferenceId;
    @Lob
    @Deprecated
    @Column(name = "CONTENT")
    private byte[] content;
    @Lob
    @Deprecated
    @Column(name = "CHECKSUM")
    private String checksum;
    @Column(name = "DIGEST", length = 512)
    private String digest;
    @Column(name = "CONTENT_TYPE")
    private StoreType contentType;
    @Column(name = "PAYLOAD_NAME", length = 512)
    private String payloadName;
    @Column(name = "PAYLOAD_IDENTIFIER", length = 512)
    private String payloadIdentifier;
    @Lob
    @Column(name = "PAYLOAD_DESCRIPTION")
    private String payloadDescription;
    @Column(name = "PAYLOAD_MIMETYPE")
    private String payloadMimeType;
    @Column(name = "PAYLOAD_SIZE")
    private long size = -1;
    @Column(name = "CONNECTOR_MESSAGE_ID")
    private String connectorMessageId;
    @ManyToOne
    @JoinColumn(name = "MESSAGE_ID", referencedColumnName = "ID")
    private PDomibusConnectorMessage message;
    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DETACHED_SIGNATURE_ID", referencedColumnName = "ID")
    private PDomibusConnectorDetachedSignature detachedSignature;
    @jakarta.persistence.Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "DELETED")
    private Date deleted;
    @jakarta.persistence.Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "CREATED", nullable = false)
    private Date created;

    /**
     * This method is annotated with @PrePersist, indicating that it will be executed before an
     * entity is persisted to the database. It is a lifecycle callback method in JPA.
     *
     * <p>The purpose of this method is to set the 'created' field of the entity to the current
     * date.
     */
    @PrePersist
    public void prePersist() {
        this.created = new Date();
    }

    @Override
    public String toString() {
        var toString = new ToStringBuilder(this);
        toString.append("id", this.id);
        return toString.build();
    }
}
