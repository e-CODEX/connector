package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorService;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Repository
public interface DomibusConnectorServiceDao extends CrudRepository<PDomibusConnectorService, Long> {
}
