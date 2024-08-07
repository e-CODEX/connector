/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model;

import eu.domibus.connector.persistence.model.enums.UserRole;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Represents a user in the PDomibusConnector system.
 */
@Getter
@Setter
@Entity
@Table(name = PDomibusConnectorUser.TABLE_NAME)
public class PDomibusConnectorUser {
    public static final String TABLE_NAME = "DOMIBUS_CONNECTOR_USER";
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
    @Column(name = "USERNAME", nullable = false, length = 50)
    private String username;
    @Column(name = "ROLE", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @Column(name = "LOCKED", nullable = false)
    private boolean locked = false;
    @Column(name = "NUMBER_OF_GRACE_LOGINS", nullable = false)
    private Long numberOfGraceLogins = 5L;
    @Column(name = "GRACE_LOGINS_USED", nullable = false)
    private Long graceLoginsUsed = 0L;
    @Column(name = "CREATED", nullable = false)
    private Date created;
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<PDomibusConnectorUserPassword> passwords = new HashSet<>();

    /**
     * This method is annotated with @PrePersist and is called before persisting the entity to the
     * database. It initializes certain fields if they are null.
     */
    @PrePersist
    public void prePersist() {
        if (this.created == null) {
            this.created = new Date();
        }
        if (this.numberOfGraceLogins == null) {
            this.numberOfGraceLogins = 5L;
        }
        if (this.graceLoginsUsed == null) {
            this.graceLoginsUsed = 0L;
        }
    }

    @Override
    public String toString() {
        var toString = new ToStringBuilder(this);
        toString.append("id", id);
        toString.append("username", this.username);
        toString.append("role", this.role);
        toString.append("locked", this.locked);
        toString.append("created", this.created);
        return toString.build();
    }
}
