package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Repository
public interface DomibusConnectorUserDao extends CrudRepository<PDomibusConnectorUser, Long> {
    PDomibusConnectorUser findOneByUsernameIgnoreCase(String username);
}
