package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorMessageError;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Repository
public interface DomibusConnectorMessageErrorDao extends CrudRepository<PDomibusConnectorMessageError, Long> {
    List<PDomibusConnectorMessageError> findByMessage(Long messageId);
}
