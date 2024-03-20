package eu.domibus.connector.persistence.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import eu.domibus.connector.persistence.model.PDomibusConnectorMessage;
import eu.domibus.connector.persistence.model.PDomibusConnectorUser;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Repository
public interface DomibusConnectorUserDao extends CrudRepository<PDomibusConnectorUser, Long> {

	public PDomibusConnectorUser findOneByUsernameIgnoreCase(String username);
}
