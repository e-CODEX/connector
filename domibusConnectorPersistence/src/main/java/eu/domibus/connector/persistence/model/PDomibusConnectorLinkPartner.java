/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model;

import eu.domibus.connector.domain.enums.LinkType;
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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import lombok.Getter;
import lombok.Setter;

/**
 * The PDomibusConnectorLinkPartner class represents a link partner in the Domibus connector. It
 * stores information about the link name, description, enabled status, link type, properties, and
 * link configuration.
 *
 * <p>The properties are stored as a map of key-value pairs, and the link configuration is
 * represented by an instance of the PDomibusConnectorLinkConfiguration class.
 */
@Getter
@Setter
@Table(name = PDomibusConnectorLinkPartner.TABLE_NAME)
@Entity
public class PDomibusConnectorLinkPartner {
    public static final String TABLE_NAME = "DC_LINK_PARTNER";
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
    private String linkName;
    @Lob
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled = Boolean.FALSE;
    @Column(name = "LINK_TYPE", length = 20)
    private LinkType linkType;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "DC_LINK_PARTNER_PROPERTY",
        joinColumns = @JoinColumn(name = "DC_LINK_PARTNER_ID", referencedColumnName = "ID")
    )
    @MapKeyColumn(name = "PROPERTY_NAME")
    @Column(name = "PROPERTY_VALUE", length = 2048)
    private Map<String, String> properties = new HashMap<>();
    @ManyToOne
    @JoinColumn(name = "LINK_CONFIG_ID", referencedColumnName = "ID")
    private PDomibusConnectorLinkConfiguration linkConfiguration;
}
