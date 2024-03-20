package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorAction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Repository
public interface DomibusConnectorActionDao extends CrudRepository<PDomibusConnectorAction, Long> {

//    public @Nullable PDomibusConnectorAction findOne(PDomibusConnectorAction action);

}
