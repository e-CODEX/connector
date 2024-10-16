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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Represents a password associated with a user in the PDomibusConnector system.
 */
@Getter
@Setter
@Entity
@Table(name = PDomibusConnectorUserPassword.TABLE_NAME)
public class PDomibusConnectorUserPassword {
    public static final String TABLE_NAME = "DOMIBUS_CONNECTOR_USER_PWD";
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
    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private PDomibusConnectorUser user;
    @Column(nullable = false, length = 1024)
    private String password;
    @Column(nullable = false, length = 512)
    private String salt;
    @Column(name = "CURRENT_PWD", nullable = false)
    private boolean currentPassword = true;
    @Column(name = "INITIAL_PWD", nullable = false)
    private boolean initialPassword = true;
    @Column(name = "CREATED", nullable = false)
    private Date created;

    /**
     * This method is annotated with @PrePersist and is called before persisting the entity to the
     * database. It initializes the 'created' field if it is null.
     */
    @PrePersist
    public void prePersist() {
        if (this.created == null) {
            this.created = new Date();
        }
    }

    @Override
    public String toString() {
        var toString = new ToStringBuilder(this);
        toString.append("id", id);
        toString.append("username", this.user.getUsername());
        toString.append("password", this.password);
        toString.append("currentPassword", this.currentPassword);
        toString.append("initialPassword", this.initialPassword);
        toString.append("created", this.created);
        return toString.build();
    }
}
