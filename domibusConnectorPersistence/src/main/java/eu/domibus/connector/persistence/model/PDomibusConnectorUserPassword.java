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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
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
