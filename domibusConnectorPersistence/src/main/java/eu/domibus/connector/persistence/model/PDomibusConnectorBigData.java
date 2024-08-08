/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * The {@code PDomibusConnectorBigData} class represents a persistent entity for storing big data
 * associated with a Domibus connector message. It is mapped to the "DOMIBUS_CONNECTOR_BIGDATA"
 * table in the database.
 *
 * <p>Use this class to interact with the big data content associated with a Domibus message.
 */
@Getter
@Setter
@Entity
@Table(name = PDomibusConnectorBigData.TABLE_NAME)
public class PDomibusConnectorBigData {
    public static final String TABLE_NAME = "DOMIBUS_CONNECTOR_BIGDATA";
    @Id
    @Column(name = "ID")
    @TableGenerator(
        name = "seq" + TABLE_NAME,
        table = PDomibusConnectorPersistenceModel.SEQ_STORE_TABLE_NAME,
        pkColumnName = PDomibusConnectorPersistenceModel.SEQ_NAME_COLUMN_NAME,
        pkColumnValue = TABLE_NAME + ".ID",
        valueColumnName = PDomibusConnectorPersistenceModel.SEQ_VALUE_COLUMN_NAME,
        initialValue = PDomibusConnectorPersistenceModel.INITIAL_VALUE,
        allocationSize = PDomibusConnectorPersistenceModel.ALLOCATION_SIZE_BULK
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seq" + TABLE_NAME)
    private Long id;
    @Lob
    @Column(name = "NAME")
    private String name;
    @Column(name = "LAST_ACCESS")
    private Date lastAccess;
    @Column(name = "MIMETYPE")
    private String mimeType;
    @Lob
    @Column(name = "CONTENT")
    private byte[] content;
    @Lob
    @Column(name = "CHECKSUM")
    private String checksum;
    @Column(name = "CREATED", nullable = false)
    private Date created;
    @Column(name = "CONNECTOR_MESSAGE_ID")
    private String connectorMessageId;

    /**
     * Method annotated with @PrePersist is executed before the entity is persisted to the database.
     * It is used to set the value of 'created' field if it is null.
     */
    @PrePersist
    public void prePersist() {
        if (created == null) {
            created = new Date();
        }
    }

    @PreUpdate
    public void preUpdate() {
        lastAccess = new Date();
    }

    @Override
    public String toString() {
        var toString = new ToStringBuilder(this);
        toString.append("id", this.id);
        toString.append("referencedMessage", this.connectorMessageId);
        return toString.build();
    }
}
