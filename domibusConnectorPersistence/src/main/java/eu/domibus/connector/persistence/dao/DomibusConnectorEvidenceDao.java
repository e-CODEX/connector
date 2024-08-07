/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorEvidence;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessage;
import eu.domibus.connector.persistence.model.enums.EvidenceType;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface provides CRUD operations for manipulating instances of PDomibusConnectorEvidence
 * class. It extends the CrudRepository interface, which provides basic CRUD operations such as
 * save, findById, delete, etc.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Repository
public interface DomibusConnectorEvidenceDao
    extends CrudRepository<PDomibusConnectorEvidence, Long> {
    @Query("SELECT e FROM PDomibusConnectorEvidence e WHERE e.businessMessage=?1 AND e.type=?2")
    public List<PDomibusConnectorEvidence> findByMessageAndEvidenceType(
        PDomibusConnectorMessage dbMessage, EvidenceType dbEvidenceType);

    @Modifying
    @Query(
        "update PDomibusConnectorEvidence e set e.deliveredToGateway=CURRENT_TIMESTAMP "
            + "WHERE e.id = ?1"
    )
    int setEvidenceDeliveredToGateway(Long id);

    @Modifying
    @Query(
        "update PDomibusConnectorEvidence e set e.deliveredToBackend=CURRENT_TIMESTAMP "
            + "WHERE e.id = ?1"
    )
    int setEvidenceDeliveredToBackend(Long id);
}
