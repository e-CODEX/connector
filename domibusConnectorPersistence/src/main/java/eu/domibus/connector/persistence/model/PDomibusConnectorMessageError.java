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
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents an error encountered during the processing of a message in the system.
 *
 * <p>The {@code PDomibusConnectorMessageError} class is an entity class that maps to the
 * {@code DOMIBUS_CONNECTOR_MSG_ERROR} table in the database. It contains properties such as the
 * unique identifier, the message associated with the error, the error message, the detailed text,
 * the error source, and the creation timestamp. The class also provides methods to get, set, and
 * manipulate these properties.
 *
 * @see PDomibusConnectorMessage
 */
@Getter
@Setter
@Entity
@Table(name = PDomibusConnectorMessageError.TABLE_NAME)
public class PDomibusConnectorMessageError {
    public static final String TABLE_NAME = "DOMIBUS_CONNECTOR_MSG_ERROR";
    private static final Logger LOGGER =
        LoggerFactory.getLogger(PDomibusConnectorMessageError.class);
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
    @OneToOne
    @JoinColumn(name = "MESSAGE_ID", nullable = false)
    private PDomibusConnectorMessage message;
    /**
     * The short message of the exception.
     */
    @Column(name = "ERROR_MESSAGE", nullable = false, length = 2048)
    private String errorMessage;
    /**
     * contains the stack trace, if given.
     */
    @Lob
    @Column(name = "DETAILED_TEXT")
    private String detailedText;
    /**
     * usually contains the full qualified class name where error happened.
     */
    @Lob
    @Column(name = "ERROR_SOURCE")
    private String errorSource;
    @Column(name = "CREATED", nullable = false)
    private Date created;

    /**
     * This method is annotated with {@code @PrePersist} and is invoked before the entity is
     * persisted into the database. It performs the necessary operations before the entity is
     * saved.
     *
     * <p>First, it checks if the {@code created} field is null. If it is null, it sets the field to
     * the
     * current date and time by creating a new {@code Date} object.
     *
     * <p>Next, it calls the {@code truncateErrorMessage} method to truncate the
     * {@code errorMessage}
     * field if its length is greater than 2047 characters.
     */
    @PrePersist
    public void prePersist() {
        if (this.created == null) {
            this.created = new Date();
        }
        truncateErrorMessage();
    }

    @PreUpdate
    public void preUpdate() {
        truncateErrorMessage();
    }

    private void truncateErrorMessage() {
        if (this.errorMessage.length() > 2047) {
            LOGGER.warn("error message exceeded maximum length -> truncated to length of 2047");
            this.errorMessage = this.errorMessage.substring(0, 2047);
        }
    }

    @Override
    public String toString() {
        var toString = new ToStringBuilder(this);
        toString.append("message", message);
        toString.append("errorMessage", errorMessage);
        toString.append("detailedText", detailedText);
        toString.append("errorSource", errorSource);
        return toString.build();
    }
}
