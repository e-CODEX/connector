/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * This class is a DAO (Data Access Object) interface that provides methods to interact with the
 * database for the DomibusConnectorUser entity. It extends the CrudRepository interface which
 * provides basic CRUD operations (Create, Read, Update, Delete).
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Repository
public interface DomibusConnectorUserDao extends CrudRepository<PDomibusConnectorUser, Long> {
    PDomibusConnectorUser findOneByUsernameIgnoreCase(String username);
}
