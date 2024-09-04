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
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * PDomibusConnectorProperties is an entity class representing Domibus connector properties.
 *
 * <p>This class is mapped to the database table {@value PDomibusConnectorProperties#TABLE_NAME}.
 */
@Getter
@Setter
@Entity
@Table(name = PDomibusConnectorProperties.TABLE_NAME)
public class PDomibusConnectorProperties implements Serializable {
    public static final String TABLE_NAME = "DOMIBUS_CONNECTOR_PROPERTY";
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
    private int id;
    @Column(name = "PROPERTY_NAME", nullable = false, length = 2048)
    private String propertyName;
    @Column(name = "PROPERTY_VALUE", length = 2048)
    private String propertyValue;

    @Override
    public String toString() {
        var toString = new ToStringBuilder(this);
        toString.append("propertyName", propertyName);
        toString.append("propertyValue", propertyValue);
        return toString.build();
    }
}
