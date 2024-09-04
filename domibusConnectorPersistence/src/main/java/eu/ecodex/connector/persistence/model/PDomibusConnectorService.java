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
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * The PDomibusConnectorService class represents a persistent object that is stored in the database
 * in the table DOMIBUS_CONNECTOR_SERVICE. It is used to store information about a service in the
 * Domibus connector, such as the ID, service, service type, and associated PModeSet. This class is
 * annotated with JPA annotations to define the mapping to the database table and columns. It also
 * includes getter and setter methods for accessing and modifying the service's properties.
 */
@Getter
@Setter
@Entity
@Table(name = PDomibusConnectorService.TABLE_NAME)
public class PDomibusConnectorService {
    public static final String TABLE_NAME = "DOMIBUS_CONNECTOR_SERVICE";
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
    @Column(name = "SERVICE", nullable = false)
    private String service;
    @Column(name = "SERVICE_TYPE")
    private String serviceType;
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
        toString.append("service", service);
        toString.append("serviceType", serviceType);
        return toString.build();
    }
}
