/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import lombok.Getter;
import lombok.Setter;

/**
 * The PDomibusConnectorLinkConfiguration class represents the configuration for a link in the
 * Domibus connector. It is used to store information about a specific link implementation,
 * configuration name, and properties.
 */
@Getter
@Setter
@Table(name = PDomibusConnectorLinkConfiguration.TABLE_NAME)
@Entity
public class PDomibusConnectorLinkConfiguration {
    public static final String TABLE_NAME = "DC_LINK_CONFIGURATION";
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
    @Column(name = "LINK_IMPL")
    private String linkImpl;
    @Column(name = "CONFIG_NAME", nullable = false)
    private String configName;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "DC_LINK_CONFIG_PROPERTY",
        joinColumns = @JoinColumn(name = "DC_LINK_CONFIGURATION_ID", referencedColumnName = "ID")
    )
    @MapKeyColumn(name = "PROPERTY_NAME")
    @Column(name = "PROPERTY_VALUE", length = 2048)
    private Map<String, String> properties = new HashMap<>();
}
