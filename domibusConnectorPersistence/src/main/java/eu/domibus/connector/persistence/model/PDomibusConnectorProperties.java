/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
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
