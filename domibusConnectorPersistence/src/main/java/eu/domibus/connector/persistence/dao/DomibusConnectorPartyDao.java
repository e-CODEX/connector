/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorParty;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface represents the data access object (DAO) for the DomibusConnectorParty entity. It
 * extends the CrudRepository interface, providing basic CRUD operations for the
 * DomibusConnectorParty entity.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Repository
public interface DomibusConnectorPartyDao extends CrudRepository<PDomibusConnectorParty, Long> {
}
