/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorAction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * DomibusConnectorActionDao is an interface that extends the CrudRepository interface. It provides
 * methods for performing CRUD operations on the PDomibusConnectorAction entity.
 *
 * <p>The PDomibusConnectorAction entity represents an action performed by the Domibus connector.
 * It has fields such as id, action, and pModeSet. It is mapped to the DOMIBUS_CONNECT OR_ACTION
 * table in the database.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Repository
public interface DomibusConnectorActionDao extends CrudRepository<PDomibusConnectorAction, Long> {
}
