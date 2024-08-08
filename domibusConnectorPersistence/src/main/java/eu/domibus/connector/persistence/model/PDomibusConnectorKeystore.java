/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model;

import eu.domibus.connector.domain.model.DomibusConnectorKeystore;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a connector keystore used by the Domibus connector.
 */
@Getter
@Setter
@Entity
@Table(name = PDomibusConnectorKeystore.TABLE_NAME)
public class PDomibusConnectorKeystore {
    public static final String TABLE_NAME = "DC_KEYSTORE";
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
    @Column(name = "UUID", nullable = false, unique = true)
    private String uuid;
    @Lob
    @Column(name = "KEYSTORE", nullable = false)
    private byte[] keystore;
    @Column(name = "PASSWORD", length = 1024)
    private String password;
    @Column(name = "UPLOADED", nullable = false)
    private Date uploaded;
    @Column(name = "DESCRIPTION", length = 512)
    private String description;
    @Column(name = "TYPE", length = 50)
    // @Enumerated(EnumType.STRING)
    private DomibusConnectorKeystore.KeystoreType type;

    /**
     * This method is annotated with @PrePersist and is part of the PDomibusConnectorKeystore class.
     * It is executed before a new instance of PDomibusConnectorKeystore is persisted to the
     * database.
     *
     * <p>The purpose of this method is to set the "uploaded" field if it is null. If the "uploaded"
     * field is null, a new Date object will be created and assigned to the field.
     */
    @PrePersist
    public void prePersist() {
        if (uploaded == null) {
            uploaded = new Date();
        }
    }
}
