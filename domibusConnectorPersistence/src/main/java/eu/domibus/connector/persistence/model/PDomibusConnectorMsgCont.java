/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model;

import eu.domibus.connector.persistence.service.impl.helper.StoreType;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.TemporalType;
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
    @javax.persistence.Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "DELETED")
    private Date deleted;
    @javax.persistence.Temporal(value = TemporalType.TIMESTAMP)
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
