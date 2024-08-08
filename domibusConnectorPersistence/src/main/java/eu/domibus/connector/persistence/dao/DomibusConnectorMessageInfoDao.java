/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorMessageInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface represents the Data Access Object (DAO) for the DomibusConnectorMessageInfo
 * entity. It extends the CrudRepository interface, providing basic CRUD operations.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Repository
public interface DomibusConnectorMessageInfoDao
    extends CrudRepository<PDomibusConnectorMessageInfo, Long> {
}
