package eu.domibus.connector.persistence.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.domibus.connector.persistence.model.enums.UserRole;

@Entity
@Table(name = PDomibusConnectorUser.TABLE_NAME)
public class PDomibusConnectorUser {

	public static final String TABLE_NAME = "DOMIBUS_CONNECTOR_USER";

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
    
    @Column(name = "USERNAME", nullable=false, length = 50)
    private String username;
    
    @Column(name = "ROLE", nullable=false, length = 50)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name="LOCKED", nullable = false)
	private boolean locked = false;

	@Column(name="NUMBER_OF_GRACE_LOGINS", nullable=false)
	private Long numberOfGraceLogins = 5L;

	@Column(name="GRACE_LOGINS_USED", nullable=false)
	private Long graceLoginsUsed = 0L;
	
    @Column(name = "CREATED", nullable = false)
    private Date created;
    
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<PDomibusConnectorUserPassword> passwords = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if(this.created==null)
        	this.created = new Date();
        if(this.numberOfGraceLogins==null)
        	this.numberOfGraceLogins = 5L;
        if(this.graceLoginsUsed==null)
        	this.graceLoginsUsed= 0L;
    }
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}



	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	public UserRole getRole() {
		return role;
	}



	public void setRole(UserRole role) {
		this.role = role;
	}



	public boolean isLocked() {
		return locked;
	}



	public void setLocked(boolean locked) {
		this.locked = locked;
	}



	public Long getNumberOfGraceLogins() {
		return numberOfGraceLogins;
	}



	public void setNumberOfGraceLogins(Long numberOfGraceLogins) {
		this.numberOfGraceLogins = numberOfGraceLogins;
	}



	public Long getGraceLoginsUsed() {
		return graceLoginsUsed;
	}



	public void setGraceLoginsUsed(Long graceLoginsUsed) {
		this.graceLoginsUsed = graceLoginsUsed;
	}



	public Date getCreated() {
		return created;
	}



	public void setCreated(Date created) {
		this.created = created;
	}



	public Set<PDomibusConnectorUserPassword> getPasswords() {
		return passwords;
	}

	public void setPasswords(Set<PDomibusConnectorUserPassword> passwords) {
		this.passwords = passwords;
	}

	@Override
    public String toString() {
        ToStringBuilder toString = new ToStringBuilder(this);
        toString.append("id", id);
        toString.append("username", this.username);
        toString.append("role", this.role);
        toString.append("locked", this.locked);
        toString.append("created", this.created);
        return toString.build();
    }

}
