/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
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
