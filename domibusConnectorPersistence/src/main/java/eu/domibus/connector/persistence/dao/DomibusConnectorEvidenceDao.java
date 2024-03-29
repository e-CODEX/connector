package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorEvidence;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessage;
import eu.domibus.connector.persistence.model.enums.EvidenceType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Repository
public interface DomibusConnectorEvidenceDao extends CrudRepository<PDomibusConnectorEvidence, Long> {
    @Query("SELECT e FROM PDomibusConnectorEvidence e WHERE e.businessMessage=?1 AND e.type=?2")
    List<PDomibusConnectorEvidence> findByMessageAndEvidenceType(
            PDomibusConnectorMessage dbMessage, EvidenceType dbEvidenceType);

    @Modifying
    @Query("update PDomibusConnectorEvidence e set e.deliveredToGateway=CURRENT_TIMESTAMP WHERE e.id = ?1")
    int setEvidenceDeliveredToGateway(Long id);

    @Modifying
    @Query("update PDomibusConnectorEvidence e set e.deliveredToBackend=CURRENT_TIMESTAMP WHERE e.id = ?1")
    int setEvidenceDeliveredToBackend(Long id);
}
