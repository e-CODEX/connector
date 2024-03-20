package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorAction;
import eu.domibus.connector.persistence.model.PDomibusConnectorProperties;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Repository
public interface DomibusConnectorPropertiesDao extends CrudRepository<PDomibusConnectorProperties, String> {

    Optional<PDomibusConnectorProperties> findByPropertyName(String propertyName);

}
