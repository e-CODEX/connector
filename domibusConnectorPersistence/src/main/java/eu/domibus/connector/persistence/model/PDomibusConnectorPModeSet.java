package eu.domibus.connector.persistence.model;

import java.sql.Blob;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;


@Entity
@Table(name = PDomibusConnectorPModeSet.TABLE_NAME)
public class PDomibusConnectorPModeSet {

    public static final String TABLE_NAME = "DC_PMODE_SET";

    @Id
    @Column(name = "ID")
    @TableGenerator(name = "seq" + TABLE_NAME,
            table = PDomibusConnectorPersistenceModel.SEQ_STORE_TABLE_NAME,
            pkColumnName = PDomibusConnectorPersistenceModel.SEQ_NAME_COLUMN_NAME,
            pkColumnValue = TABLE_NAME + ".ID",
            valueColumnName = PDomibusConnectorPersistenceModel.SEQ_VALUE_COLUMN_NAME,
            initialValue = PDomibusConnectorPersistenceModel.INITIAL_VALUE,
            allocationSize = PDomibusConnectorPersistenceModel.ALLOCATION_SIZE)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seq" + TABLE_NAME)
    private Long id;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CREATED")
    private Timestamp created;

    @ManyToOne
    @JoinColumn(name = "FK_MESSAGE_LANE", referencedColumnName = "ID")
    private PDomibusConnectorMessageLane messageLane;

    @Column(name = "ACTIVE")
    private boolean active;

    @Lob
    @Column(name = "PMODES")
    private byte[] pmodes;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "pModeSet", fetch = FetchType.EAGER)
    private Set<PDomibusConnectorParty> parties = new HashSet<>();

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "pModeSet", fetch = FetchType.EAGER)
    private Set<PDomibusConnectorAction> actions = new HashSet<>();

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "pModeSet", fetch = FetchType.EAGER)
    private Set<PDomibusConnectorService> services = new HashSet<>();
    
    @ManyToOne
    @JoinColumn(name = "FK_CONNECTORSTORE", referencedColumnName = "ID")
    private PDomibusConnectorKeystore connectorstore;

    public PDomibusConnectorMessageLane getMessageLane() {
        return messageLane;
    }

    public void setMessageLane(PDomibusConnectorMessageLane messageLane) {
        this.messageLane = messageLane;
    }

    public PDomibusConnectorKeystore getConnectorstore() {
		return connectorstore;
	}

	public void setConnectorstore(PDomibusConnectorKeystore connectorstore) {
		this.connectorstore = connectorstore;
	}

	@PrePersist
    public void prePersist() {
        this.created = Timestamp.from(Instant.now());
        this.parties.forEach(p -> p.setpModeSet(this));
        this.actions.forEach(a -> a.setpModeSet(this));
        this.services.forEach(s -> s.setpModeSet(this));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Set<PDomibusConnectorParty> getParties() {
        return parties;
    }

    public void setParties(Set<PDomibusConnectorParty> parties) {
        this.parties = parties;
    }

    public Set<PDomibusConnectorAction> getActions() {
        return actions;
    }

    public void setActions(Set<PDomibusConnectorAction> actions) {
        this.actions = actions;
    }

    public Set<PDomibusConnectorService> getServices() {
        return services;
    }

    public void setServices(Set<PDomibusConnectorService> services) {
        this.services = services;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public byte[] getPmodes() {
        return pmodes;
    }

    public void setPmodes(byte[] pmodes) {
        this.pmodes = pmodes;
    }
}

