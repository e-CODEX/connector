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

import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * In the future the connector will support multiple UseCases, Backends, Gateways This Entity
 * describes a configuration set for message processing and will be used to process the message.
 */
@Getter
@Setter
@Entity
@Table(name = PDomibusConnectorMessageLane.TABLE_NAME)
public class PDomibusConnectorMessageLane {
    public static final String TABLE_NAME = "DC_MESSAGE_LANE";
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
    @Column(name = "NAME", unique = true, nullable = false)
    private DomibusConnectorBusinessDomain.BusinessDomainId name;
    @Lob
    @Column(name = "DESCRIPTION")
    private String description;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "DC_MESSAGE_LANE_PROPERTY",
        joinColumns = @JoinColumn(name = "DC_MESSAGE_LANE_ID", referencedColumnName = "ID")
    )
    @MapKeyColumn(name = "PROPERTY_NAME")
    @Column(name = "PROPERTY_VALUE", length = 2048)
    private Map<String, String> properties = new HashMap<>();
}
