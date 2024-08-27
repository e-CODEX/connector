/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * The PDomibusConnectorAction class represents a connector action in the Domibus system. It is used
 * to define the action that needs to be taken for a certain PMode set.
 *
 * <p>The class is annotated with @Entity and @Table, indicating that it is a JPA entity mapped to
 * a database table. The table name is specified by the constant TABLE_NAME.
 */
@Getter
@Setter
@Entity
@Table(name = PDomibusConnectorAction.TABLE_NAME)
public class PDomibusConnectorAction implements Serializable {
    public static final String TABLE_NAME = "DOMIBUS_CONNECTOR_ACTION";
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
    @Column(name = "ACTION", nullable = false)
    private String action;
    @SuppressWarnings("checkstyle:MemberName")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_PMODE_SET", referencedColumnName = "ID")
    private PDomibusConnectorPModeSet pModeSet;

    @SuppressWarnings("checkstyle:ParameterName")
    public void setpModeSet(PDomibusConnectorPModeSet pModeSet) {
        this.pModeSet = pModeSet;
    }

    @Override
    public String toString() {
        var toString = new ToStringBuilder(this);
        toString.append("action", action);
        return toString.build();
    }
}
