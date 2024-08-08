/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import lombok.Getter;
import lombok.Setter;

/**
 * The PDomibusConnectorPModeSet class represents a persistent object that is stored in the database
 * in the table DC_PMODE_SET. It is used to store information about a PModeSet in the Domibus
 * connector, such as the ID, description, created timestamp, active status, parties, actions, and
 * services associated with the PModeSet. This class is annotated with JPA annotations to define the
 * mapping to the database table and columns. It also includes getter and setter methods for
 * accessing and modifying the PModeSet's properties.
 */
@Getter
@Setter
@Entity
@Table(name = PDomibusConnectorPModeSet.TABLE_NAME)
public class PDomibusConnectorPModeSet {
    public static final String TABLE_NAME = "DC_PMODE_SET";
    @Id
    @Column(name = "ID")
    @TableGenerator(
        name = "seq" + TABLE_NAME,
        table = PDomibusConnectorPersistenceModel.SEQ_STORE_TABLE_NAME,
        pkColumnName = PDomibusConnectorPersistenceModel.SEQ_NAME_COLUMN_NAME,
        pkColumnValue = TABLE_NAME + ".ID",
        valueColumnName = PDomibusConnectorPersistenceModel.SEQ_VALUE_COLUMN_NAME,
        initialValue = PDomibusConnectorPersistenceModel.INITIAL_VALUE,
        allocationSize = PDomibusConnectorPersistenceModel.ALLOCATION_SIZE
    )
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

    /**
     * This method is annotated with @PrePersist, indicating that it is a lifecycle callback method
     * that is invoked before a new entity is persisted into the database. The purpose of this
     * method is to perform pre-persistence operations on the PDomibusConnectorPModeSet entity.
     *
     * <p>This method sets the created timestamp of the PDomibusConnectorPModeSet instance to the
     * current timestamp using the Timestamp.from(Instant) method. It also iterates over the
     * parties, actions, and services associated with the PDomibusConnectorPModeSet and sets their
     * pModeSet property to the current instance.
     */
    @PrePersist
    public void prePersist() {
        this.created = Timestamp.from(Instant.now());
        this.parties.forEach(p -> p.setpModeSet(this));
        this.actions.forEach(a -> a.setpModeSet(this));
        this.services.forEach(s -> s.setpModeSet(this));
    }
}

