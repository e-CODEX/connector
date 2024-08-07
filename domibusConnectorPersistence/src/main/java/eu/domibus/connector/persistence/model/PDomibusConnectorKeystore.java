package eu.domibus.connector.persistence.model;

import java.sql.Blob;
import java.util.Date;

import jakarta.persistence.*;

import eu.domibus.connector.domain.model.DomibusConnectorKeystore;

@Entity
@Table(name = PDomibusConnectorKeystore.TABLE_NAME)
public class PDomibusConnectorKeystore {

	public static final String TABLE_NAME = "DC_KEYSTORE";

	@Id
	@Column(name="ID")
	@TableGenerator(name = "seq" + TABLE_NAME,
		table = PDomibusConnectorPersistenceModel.SEQ_STORE_TABLE_NAME,
		pkColumnName = PDomibusConnectorPersistenceModel.SEQ_NAME_COLUMN_NAME,
		pkColumnValue = TABLE_NAME + ".ID",
		valueColumnName = PDomibusConnectorPersistenceModel.SEQ_VALUE_COLUMN_NAME,
		initialValue = PDomibusConnectorPersistenceModel.INITIAL_VALUE,
		allocationSize = PDomibusConnectorPersistenceModel.ALLOCATION_SIZE)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "seq" + TABLE_NAME)
	private Long id;
	
	@Column(name="UUID", nullable=false, unique=true)
	private String uuid;

	@Lob
	@Column(name="KEYSTORE", nullable=false)
    private byte[] keystore;

	@Column(name = "PASSWORD", length = 1024)
	private String password;
	
	@Column(name="UPLOADED", nullable = false)
    private Date uploaded;
	
	@Column(name="DESCRIPTION", length = 512)
	private String description;
	
	@Column(name="TYPE", length = 50)
//	@Enumerated(EnumType.STRING)
	private DomibusConnectorKeystore.KeystoreType type;
	
	@PrePersist
    public void prePersist() {
        if(uploaded == null) 
        	uploaded = new Date();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public byte[] getKeystore() {
		return keystore;
	}

	public void setKeystore(byte[] keystore) {
		this.keystore = keystore;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getUploaded() {
		return uploaded;
	}

	public void setUploaded(Date uploaded) {
		this.uploaded = uploaded;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DomibusConnectorKeystore.KeystoreType getType() {
		return type;
	}

	public void setType(DomibusConnectorKeystore.KeystoreType type) {
		this.type = type;
	}



	
}
