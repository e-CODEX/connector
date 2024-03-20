package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorMessageError;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Repository
public interface DomibusConnectorMessageErrorDao extends CrudRepository<PDomibusConnectorMessageError, Long> {

    public List<PDomibusConnectorMessageError> findByMessage(Long messageId);
    
}
