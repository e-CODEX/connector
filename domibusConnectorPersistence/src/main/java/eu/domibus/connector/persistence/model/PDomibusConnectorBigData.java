package eu.domibus.connector.persistence.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.persistence.*;
import java.sql.Blob;
import java.util.Date;

@Entity
@Table(name = PDomibusConnectorBigData.TABLE_NAME)
public class PDomibusConnectorBigData {

    public static final String TABLE_NAME = "DOMIBUS_CONNECTOR_BIGDATA";

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

    @Lob
    @Column(name="NAME")
    private String name;

    @Column(name="LAST_ACCESS")
    private Date lastAccess;

    @Column(name="MIMETYPE")
    private String mimeType;

    @Lob
    @Column(name="CONTENT")
    private byte[] content;

    @Lob
    @Column(name="CHECKSUM")
    private String checksum;
    
    @Column(name="CREATED", nullable = false)
    private Date created;

    @Column(name="CONNECTOR_MESSAGE_ID")
    private String connectorMessageId;
    
    @PrePersist
    public void prePersist() {
        if(created == null) 
            created = new Date();
    }
    
    @PreUpdate
    public void preUpdate() {
    	lastAccess = new Date();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Date lastAccess) {
        this.lastAccess = lastAccess;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
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

    public String getConnectorMessageId() {
        return connectorMessageId;
    }

    public void setConnectorMessageId(String connectorMessageId) {
        this.connectorMessageId = connectorMessageId;
    }

    public Date getCreated() {
        return created;
    }

    @Override
    public String toString() {
        ToStringBuilder toString = new ToStringBuilder(this);
        toString.append("id", this.id);
        toString.append("referencedMessage", this.connectorMessageId);
        return toString.build();
    }
}
