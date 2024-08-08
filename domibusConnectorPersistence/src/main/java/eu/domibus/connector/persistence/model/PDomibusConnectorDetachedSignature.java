/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model;

import eu.domibus.connector.domain.model.DetachedSignatureMimeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import lombok.Getter;
import lombok.Setter;

/**
 * The PDomibusConnectorDetachedSignature class represents a detached signature in the Domibus
 * Connector. It is used to store information about the signature and its related properties.
 *
 * <p>The PDomibusConnectorDetachedSignature class is used by the PDomibusConnectorMsgCont class to
 * store the detached signature of a message. It is accessed through the getDetachedSignature() and
 * setDetachedSignature(PDomibusConnectorDetachedSignature detachedSignature) methods of the
 * PDomibusConnectorMsgCont class.
 */
@Getter
@Setter
@Entity
@Table(name = PDomibusConnectorDetachedSignature.TABLE_NAME)
public class PDomibusConnectorDetachedSignature {
    public static final String TABLE_NAME = "DC_MSGCNT_DETSIG";
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
    @Column(name = "SIGNATURE")
    private byte[] detachedSignature;
    @Column(name = "SIGNATURE_NAME")
    private String detachedSignatureName;
    @Column(name = "SIGNATURE_TYPE")
    private DetachedSignatureMimeType mimeType;
}
