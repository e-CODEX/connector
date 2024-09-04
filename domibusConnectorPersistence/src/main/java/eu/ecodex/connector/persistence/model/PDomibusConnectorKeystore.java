/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.model;

import eu.ecodex.connector.domain.model.DomibusConnectorKeystore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import java.util.Date;
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
     * <p>The purpose of this method is to set the "uploaded" field if it is null. If the
     * "uploaded"
     * field is null, a new Date object will be created and assigned to the field.
     */
    @PrePersist
    public void prePersist() {
        if (uploaded == null) {
            uploaded = new Date();
        }
    }
}
