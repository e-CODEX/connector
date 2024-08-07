package eu.domibus.connector.persistence.model;

import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.persistence.service.impl.helper.StoreType;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.*;

/**
 *  This class stores message content metadata for
 *   <ul>
 *      <li>message attachments</li>
 *      <li>message content xml</li>
 *      <li>message content document</li>
 *   </ul>
 *
 *   The storage itself is delegated to a storage provider
 *    the name and reference of the storage provider is also stored
 * 
 * 
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Entity
@Table(name="DOMIBUS_CONNECTOR_MSG_CONT")
public class PDomibusConnectorMsgCont {

    public static final String TABLE_NAME = "DOMIBUS_CONNECTOR_MSG_CONT";

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

    @Column(name = "STORAGE_PROVIDER_NAME")
    private String storageProviderName;

    @Column(name = "STORAGE_REFERENCE_ID", length = 512)
    private String storageReferenceId;

    @Lob
    @Deprecated
    @Column(name="CONTENT")
    private byte[] content;

    @Lob
    @Deprecated
    @Column(name="CHECKSUM")
    private String checksum;

    @Column(name="DIGEST", length = 512)
    private String digest;
    
    @Column(name="CONTENT_TYPE")
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

    @OneToOne(optional = true, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DETACHED_SIGNATURE_ID", referencedColumnName = "ID")
    private PDomibusConnectorDetachedSignature detachedSignature;

    @jakarta.persistence.Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "DELETED")
    private Date deleted;

    @jakarta.persistence.Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "CREATED", nullable = false)
    private Date created;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @PrePersist
    public void prePersist() {
        this.created = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPayloadName() {
        return payloadName;
    }

    public void setPayloadName(String payloadName) {
        this.payloadName = payloadName;
    }

    public String getPayloadMimeType() {
        return payloadMimeType;
    }

    public void setPayloadMimeType(String payloadMimeType) {
        this.payloadMimeType = payloadMimeType;
    }

    public String getPayloadIdentifier() {
        return payloadIdentifier;
    }

    public void setPayloadIdentifier(String payloadIdentifier) {
        this.payloadIdentifier = payloadIdentifier;
    }

    public String getPayloadDescription() {
        return payloadDescription;
    }

    public void setPayloadDescription(String payloadDescription) {
        this.payloadDescription = payloadDescription;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public StoreType getContentType() {
        return contentType;
    }

    public void setContentType(StoreType contentType) {
        this.contentType = contentType;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getStorageProviderName() {
        return storageProviderName;
    }

    public void setStorageProviderName(String storageProviderName) {
        this.storageProviderName = storageProviderName;
    }

    public String getStorageReferenceId() {
        return storageReferenceId;
    }

    public PDomibusConnectorDetachedSignature getDetachedSignature() {
        return detachedSignature;
    }

    public void setDetachedSignature(PDomibusConnectorDetachedSignature detachedSignature) {
        this.detachedSignature = detachedSignature;
    }

    public void setStorageReferenceId(String storageReferenceId) {
        this.storageReferenceId = storageReferenceId;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public String getConnectorMessageId() {
        return connectorMessageId;
    }

    public void setConnectorMessageId(String messageId) {
        this.connectorMessageId = messageId;
    }

    public PDomibusConnectorMessage getMessage() {
        return message;
    }

    public void setMessage(PDomibusConnectorMessage message) {
        this.message = message;
    }

    @Override
    public String toString() {
        ToStringBuilder toString = new ToStringBuilder(this);
        toString.append("id", this.id);
        return toString.build();
    }

}
